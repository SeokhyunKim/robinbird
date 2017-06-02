package org.robinbird.model;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by seokhyun on 5/31/17.
 */
@Data
public class Member {

	@NonNull protected AccessModifier accessModifier;

	@NonNull protected Type type;

	@NonNull protected String name;
}
