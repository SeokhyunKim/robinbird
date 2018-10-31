package org.robinbird.main.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Created by seokhyun on 5/31/17.
 */
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Member {

	@NonNull protected final AccessModifier accessModifier;

	@NonNull protected final Type type;

	@NonNull protected final String name;
}
