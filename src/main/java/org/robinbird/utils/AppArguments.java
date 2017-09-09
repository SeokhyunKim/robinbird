package org.robinbird.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.robinbird.presentation.PresentationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.utils.Msgs.Key.*;

/**
 * Created by seokhyun on 5/26/17.
 */
@Slf4j
@Getter
@Builder
@ToString
public class AppArguments {

	private enum ArgType {
		ROOT1("-r"), ROOT2("--root"),
		PRESENTATION1("-p"), PRESENTATION2("--presentation"),
		TERMINAL_CLASS1("-tc"), TERMINAL_CLASS2("--terminal-class"),
		EXCLUDED_CLASS1("-ec"), EXCLUDED_CLASS2("--excluded-class");

		private final String name;
		private static Map<String, ArgType> argTypeMap;
		static {
			argTypeMap = Arrays.stream(ArgType.values())
				.collect(Collectors.toMap(e -> e.getName(), e -> e));
		}

		ArgType(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public static ArgType getArgType(String s) {
			return argTypeMap.get(s);
		}
	}

	@NonNull private String sourceRootPath;
	@NonNull private PresentationType presentationType;
	@NonNull private List<Pattern> terminalClassPatterns;
	@NonNull private List<Pattern> excludedClassPatterns;

	public static AppArguments parseArguments(String[] args) throws IllegalArgumentException {
		AppArgumentsBuilder argsBuilder = AppArguments.builder();
		//default values
		argsBuilder.presentationType(PresentationType.PLANTUML);
		List<Pattern> terminalPatterns = new ArrayList<>();
		List<Pattern> excludePatterns = new ArrayList<>();
		argsBuilder.terminalClassPatterns(terminalPatterns);
		argsBuilder.excludedClassPatterns(excludePatterns);
		// parse parameters
		for (int i=0; i<args.length;) {
			ArgType at = ArgType.getArgType(args[i]);
			if (at == null) {
				++i;
				continue;
			}
			switch (at) {
				case ROOT1:
				case ROOT2:
					checkState(i+1<args.length, Msgs.get(ROOT_SOURCE_PATH_NOT_PROVIDED));
					argsBuilder.sourceRootPath(args[i+1]);
					i += 2;
					break;
				case PRESENTATION1:
				case PRESENTATION2:
					try {
						if (i+1 >= args.length) { throw new IllegalArgumentException(); }
						PresentationType ptype = PresentationType.valueOf(args[i + 1]);
						argsBuilder.presentationType(ptype);
					} catch (IllegalArgumentException e) {
						log.warn(Msgs.get(PRESENTATION_OPTION_IS_NOT_VALID, (i+1<args.length ? args[i+1] : "Not given") + " Using default value."));
					}
					i += 2;
					break;
				case TERMINAL_CLASS1:
				case TERMINAL_CLASS2:
				case EXCLUDED_CLASS1:
				case EXCLUDED_CLASS2:
					List<Pattern> patterns;
					Msgs.Key keyForIllegarArg, keyForPatternSyntax;
					if(at==ArgType.TERMINAL_CLASS1 || at==ArgType.TERMINAL_CLASS2) {
						patterns = terminalPatterns;
						keyForIllegarArg = REGEXP_FOR_TERMINAL_IS_NOT_GIVEN;
						keyForPatternSyntax = WRONG_REGEXP_FOR_TERMINAL;
					} else {
						patterns = excludePatterns;
						keyForIllegarArg = REGEXP_FOR_EXCLUSION_IS_NOT_GIVEN;
						keyForPatternSyntax = WRONG_REGEXP_FOR_EXCLUSION;
					}
					try {
						if (args.length <= i+1) { throw new IllegalArgumentException(); }
						patterns.add(Pattern.compile(args[i + 1]));
					} catch (PatternSyntaxException e) {
						log.warn(Msgs.get(keyForPatternSyntax, args[i+1]));
					} catch (IllegalArgumentException e) {
						log.warn(Msgs.get(keyForIllegarArg));
					}
					i += 2;
					break;
			}
		}
		AppArguments appArgs = argsBuilder.build();
		checkState(StringUtils.isNotEmpty(appArgs.getSourceRootPath()), Msgs.get(ROOT_SOURCE_PATH_NOT_PROVIDED));
		return appArgs;
	}
}
