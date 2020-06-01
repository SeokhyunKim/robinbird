package org.robinbird.model;

import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;
import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;
import static org.robinbird.util.Msgs.Key.INVALID_COMPONENT_CATEGORY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.robinbird.repository.RbRepository;
import org.robinbird.util.Msgs;

@Slf4j
public class AnalysisContext {

    private final RbRepository repository;
    private final Stack<Class> currentComponents = new Stack<>();

    @Getter @Setter
    private Package currentPackage;

    @Getter @Setter
    private List<Pattern> terminalClassPatterns;

    @Getter @Setter
    private List<Pattern> excludedClassPatterns;

    @Getter @Setter
    private boolean isParsingEnum;

    public AnalysisContext(@NonNull final RbRepository repository) {
        this.repository = repository;
    }

    public void pushCurrent(@NonNull final Class component) {
        Validate.isTrue(isPushableCategory(component),
                        Msgs.get(INVALID_COMPONENT_CATEGORY, component.getComponentCategory().name()));

        if (currentPackage != null) {
            currentPackage.addClass(component);
            component.setPackage(currentPackage);
        }
        currentComponents.push(component);
    }

    private boolean isPushableCategory(@NonNull final Component component) {
        final ComponentCategory category = component.getComponentCategory();
        return (category == ComponentCategory.CLASS || category == ComponentCategory.INTERFACE ||
               category == ComponentCategory.TEMPLATE_CLASS);
    }

    public void popCurrent() {
        if (currentComponents.empty()) {
            return;
        }
        currentComponents.pop();
    }

    public Class getCurrent() {
        if (currentComponents.empty()) {
            return null;
        }
        return currentComponents.peek();
    }

    // todo: currently, this name is same with sort of simple name which means class name without package name.
    //       it is a limitation definitely. Need to visit this again later to use full name like package name + class name.
    public Optional<Component> getComponent(@NonNull final String name) {
        return repository.getComponent(name);
    }

    public List<Component> getComponents(@NonNull final ComponentCategory componentCategory) {
        return repository.getComponents(componentCategory);
    }

    public List<Component> getComponents(@NonNull final Collection<ComponentCategory> categories) {
        List<Component> components = new ArrayList<>();
        categories.forEach(category -> components.addAll(getComponents(category)));
        return components;
    }

    // todo: name should be a full name
    private Component register(@NonNull final String name, @NonNull final ComponentCategory category) {
        log.debug("register Component: {}, {}", name, category);
        final Optional<Component> componentOpt = repository.getComponent(name);
        if (componentOpt.isPresent()) {
            final Component component = componentOpt.get();
            if (component.getComponentCategory() != category) {
                component.updateComponentCategory(category);
                repository.updateComponentWithoutChangingRelations(component);
            }
            return component;
        }
        final Component newComponent = repository.registerComponent(name, category);
        log.info("Newly registered Component: {}", newComponent);
        return newComponent;
    }

    /**
     * When there is a component already with the given name, returns it.
     * If there's no component with the given name, creating new one with {@link Class} type.
     * It can be changed when the type is really parsed.
     * @param name a name of {@link Component} to register.
     * @return registered {@link Component} with the given name.
     */
    public Component register(@NonNull final String name) {
        final Optional<Component> componentOpt = repository.getComponent(name);
        if (componentOpt.isPresent()) {
            return componentOpt.get();
        }
        final Component newComponent = repository.registerComponent(name, ComponentCategory.CLASS);
        log.info("Newly registered Component: {}", newComponent);
        return newComponent;
    }

    public Component registerPrimitiveType(@NonNull final String name) {
        return register(name, ComponentCategory.PRIMITIVE_TYPE);
    }

    public Class registerClass(@NonNull final String name, @NonNull ComponentCategory category) {
        Validate.isTrue(category == ComponentCategory.CLASS ||
                        category == ComponentCategory.TEMPLATE_CLASS ||
                        category == ComponentCategory.INTERFACE,
                        Msgs.get(INVALID_COMPONENT_CATEGORY, category.name()));
        final Component component = register(name, category);
        return Class.builder()
                    .id(component.getId())
                    .name(name)
                    .category(category)
                    .build();
    }

    public Package registerPackage(@NonNull final List<String> packageNameList) {
        final String packageName = Package.createPackageName(packageNameList);
        final Optional<Component> compOpt = repository.getComponent(packageName);
        if (compOpt.isPresent()) {
            final Component comp = compOpt.get();
            Validate.isTrue(comp.getComponentCategory() == ComponentCategory.PACKAGE,
                            Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, packageName, comp.getComponentCategory().name()));
            return Package.create(comp);
        }
        final Component newPackageComponent = repository.registerComponent(packageName, ComponentCategory.PACKAGE);
        return Package.create(newPackageComponent);
    }

    public Container registerContainer(@NonNull final String collectionTypeName, @NonNull final List<Component> types) {
        log.debug("collectionTypeName={}, types={}", collectionTypeName, types);
        Validate.isTrue(getCurrent() != null, Msgs.get(INTERNAL_ERROR));
        final Component owner = getCurrent();
        final Optional<Component> baseTypeCompOpt = repository.getComponent(collectionTypeName);
        final Component baseTypeComp;
        if (baseTypeCompOpt.isPresent()) {
            baseTypeComp = baseTypeCompOpt.get();
        } else {
            // register a collection as TEMPLATE_CLASS
            baseTypeComp = repository.registerComponent(collectionTypeName, ComponentCategory.TEMPLATE_CLASS);
        }
        final String containerName = Container.createContainerName(collectionTypeName, types);
        final Optional<Component> containerCompOpt = repository.getDependentComponent(containerName, owner);
        final Component containerComp;
        final Container container;
        if (containerCompOpt.isPresent()) {
            containerComp = containerCompOpt.get();
            container = Container.create(containerComp);
        } else {
            containerComp = repository.registerDependentComponent(containerName, ComponentCategory.CONTAINER, owner);
            container = Container.create(containerComp.getId(),
                                         baseTypeComp,
                                         types,
                                         owner);
            log.debug("Trying to persist a new collection: {}", container);
            CurrentRbRepository.persist(container);
        }

        return container;
    }

    public Array registerPrimitiveTypeArray(@NonNull final String typeName) {
        final Component primitiveComp = registerPrimitiveType(typeName);
        return registerArray(primitiveComp);
    }

    public Array registerReferenceTypeArray(@NonNull final String typeName) {
        final Component comp = register(typeName);
        return registerArray(comp);
    }

    private Array registerArray(@NonNull final Component baseType) {
        Validate.isTrue(getCurrent() != null, Msgs.get(INTERNAL_ERROR));
        final Component owner = getCurrent();
        final String arrayName = Array.createArrayName(baseType);
        final Optional<Component> componentOpt = repository.getDependentComponent(arrayName, owner);
        if (componentOpt.isPresent()) {
            final Component component = componentOpt.get();
            Validate.isTrue(component.getComponentCategory() == ComponentCategory.ARRAY,
                            Msgs.get(INTERNAL_ERROR));

            return Array.create(component);
        }
        final Component arrayComp = repository.registerDependentComponent(arrayName, ComponentCategory.ARRAY, owner);
        final Array array = Array.create(arrayComp.getId(), baseType, owner);
        CurrentRbRepository.persist(array);
        return array;
    }

    public Varargs registerVarargs(@NonNull final Component baseType) {
        Validate.isTrue(getCurrent() != null, Msgs.get(INTERNAL_ERROR));
        final Component owner = getCurrent();
        final String varargsName = Varargs.createVarargsName(baseType);
        final Optional<Component> componentOpt = repository.getDependentComponent(varargsName, owner);
        if (componentOpt.isPresent()) {
            final Component component = componentOpt.get();
            Validate.isTrue(component.getComponentCategory() == ComponentCategory.VARARGS,
                            Msgs.get(INTERNAL_ERROR));
            return Varargs.create(component);
        }
        final Component varargsComp = repository.registerDependentComponent(varargsName, ComponentCategory.VARARGS, owner);
        final Varargs varargs = Varargs.create(varargsComp.getId(), baseType, owner);
        CurrentRbRepository.persist(varargs);
        return varargs;
    }

    public Function registerFunction(@NonNull final String functionName, @NonNull final List<Component> params,
                                     @NonNull final Component returnType) {
        Validate.isTrue(getCurrent() != null, Msgs.get(INTERNAL_ERROR));
        final Component owner = getCurrent();
        final String funcNameWithParams = Function.createFunctionName(functionName, params);
        final Optional<Component> componentOpt = repository.getDependentComponent(funcNameWithParams, owner);
        if (componentOpt.isPresent()) {
            final Component component = componentOpt.get();
            Validate.isTrue(component.getComponentCategory() == ComponentCategory.FUNCTION,
                            Msgs.get(INTERNAL_ERROR));
            return Function.create(component);
        }
        final Component functionComp = repository.registerDependentComponent(funcNameWithParams, ComponentCategory.FUNCTION, owner);
        final Function function = Function.create(functionComp.getId(), returnType, functionName, params, owner);
        CurrentRbRepository.persist(function);
        return function;
    }

    public Class getClass(@NonNull final String className) {
        final Optional<Component> compOpt = repository.getComponent(className);
        return compOpt.map(c -> Class.builder()
                                     .id(c.getId())
                                     .name(c.getName())
                                     .category(c.getComponentCategory())
                                     .build())
                      .orElse(null);
    }

    public boolean isTerminal(String identifier) {
        if (terminalClassPatterns == null) { return false; }
        for (Pattern pattern : terminalClassPatterns) {
            if (pattern.matcher(identifier).matches()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentTerminal() {
        if (getCurrent() == null) {
            return false;
        }
        return isTerminal(getCurrent().getName());
    }

    public boolean isExcluded(String identifier) {
        if (excludedClassPatterns == null) {
            return false;
        }
        for (Pattern pattern : excludedClassPatterns) {
            if (pattern.matcher(identifier).matches()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentExcluded() {
        if (getCurrent() == null) { return false; }
        return isExcluded(getCurrent().getName());
    }

}
