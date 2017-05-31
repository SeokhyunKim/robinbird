package org.robinbird.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.utils.Msgs.Key.ROOT_SOURCE_PATH_NOT_PROVIDED;

/**
 * Created by seokhyun on 5/26/17.
 */
@Getter
@Builder
@ToString
public class AppArguments {

	private enum ArgType {
		ROOT1("-r"), ROOT2("--root");

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

	@NonNull
	private String sourceRootPath;

	public static AppArguments parseArguments(String[] args) throws IllegalArgumentException {
		AppArgumentsBuilder argsBuilder = AppArguments.builder();
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
				default:
					++i;
					break;
			}
		}
		AppArguments appArgs = argsBuilder.build();
		checkState(StringUtils.isNotEmpty(appArgs.getSourceRootPath()), Msgs.get(ROOT_SOURCE_PATH_NOT_PROVIDED));
		return appArgs;
	}
}
