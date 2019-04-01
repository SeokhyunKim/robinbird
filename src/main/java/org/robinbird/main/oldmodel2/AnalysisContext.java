package org.robinbird.main.oldmodel2;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.robinbird.main.oldrepository.TypeRepository;
import org.robinbird.util.Msgs;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.robinbird.util.Msgs.Key.INVALID_TYPE_CATEGORY;

@Slf4j
@RequiredArgsConstructor
public class AnalysisContext {
	
	private final TypeRepository types;
	private final Stack<Type> currentClasses = new Stack<>();
	
	@Getter @Setter
	private Type currentPackage;
	
	@Getter @Setter
	private List<Pattern> terminalClassPatterns;
	
	@Getter @Setter
	private List<Pattern> excludedClassPatterns;

	@Getter @Setter
	private boolean isParsingEnum;
	
	public void pushCurrentClass(final @NonNull Type c) {
		Validate.isTrue(c.getCategory() == TypeCategory.CLASS, Msgs.get(INVALID_TYPE_CATEGORY, c.getCategory().name()));
		if (currentPackage != null) {
			currentPackage.addRelation(Relation.create(RelationCategory.PACKAGE_MEMBER, c));
		}
		currentClasses.push(c);
	}
	
	public Type popCurrentClass() {
		if (currentClasses.empty()) {
			return null;
		}
		return currentClasses.pop();
	}
	
	public Type getCurrentClass() {
		if (currentClasses.empty()) {
			return null;
		}
		return currentClasses.peek();
	}
	
	public Type getType(@NonNull final String name) {
		return types.getType(name).orElse(null);
	}
	
	public Type getType(@NonNull final String name, @NonNull final TypeCategory tc) {
		Optional<Type> typeOpt = types.getType(name);
		if (!typeOpt.isPresent() || typeOpt.get().getCategory() != tc) {
			return null;
		}
		return typeOpt.get();
	}

	public List<Type> getClasses() {
		return types.getTypes(TypeCategory.CLASS);
	}
	
	public Type registerType(@NonNull final String name, @NonNull final TypeCategory tc) {
		log.debug("register type: {}, {}", name, tc.name());
		final Type alreadyRegistered = getType(name);
		if (alreadyRegistered != null) {
		    // One possible case is registered as CLASS and updated as INTERFACE later.
            // For now, don't check this case and just update category.
//		    if (alreadyRegistered.getCategory() != tc) {
//		        types.updateType(alreadyRegistered.updateTypeCategory(AnalysisEntityCategory.INTERFACE);
//            }
			return alreadyRegistered;
		}
		final Type newType = types.registerType(tc,  name);
		log.info("newly registered type: {}", newType);
		return newType;
	}

	public Type registerMethod(@NonNull final Type parentType, @NonNull AccessModifier accessModifier,
							   @NonNull String methodName, @NonNull final List<Type> params) {
		log.debug("register method: {}, {}, {}, {}", parentType.getName(), accessModifier, methodName, params);
//		Type curParentType = types.populateType(parentType);
//		curParentType.addRelation(Relation.builder().category(RelationCategory.MEMBER_FUNCTION)..build());

		return null;


	}

	public Type registerCollection(@NonNull final String name, @NonNull final List<Type> refTypes) {
		log.debug("register collection: name={}, refTypes={}", name, refTypes);

		final Type newType = types.registerType(TypeCategory.COLLECTION, name);

		//newType.addCompositionTypes(refTypes);
		types.updateType(newType);
		return newType;
	}

	public Type registerPackage(@NonNull final List<String> packageNameList) {
        final StringBuffer sb = new StringBuffer();
        sb.append(packageNameList.get(0));
        for (int i=1; i<packageNameList.size(); ++i) {
            sb.append(".").append(packageNameList.get(i));
        }
        final String packageName = sb.toString();
        return registerType(packageName, TypeCategory.PACKAGE);
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

	public boolean isCurrentClassTerminal() {
		if (getCurrentClass() == null) { return false; }
		return isTerminal(getCurrentClass().getName());
	}

	public boolean isExcluded(String identifier) {
		if (excludedClassPatterns == null) { return false; }
		for (Pattern pattern : excludedClassPatterns) {
			if (pattern.matcher(identifier).matches()) {
				return true;
			}
		}
		return false;
	}

	public boolean isCurrentClassExcluded() {
		if (getCurrentClass() == null) { return false; }
		return isExcluded(getCurrentClass().getName());
	}
	
}

