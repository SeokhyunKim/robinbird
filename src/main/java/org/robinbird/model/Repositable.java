package org.robinbird.model;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by seokhyun on 6/2/17.
 */
@Data
public class Repositable {

	protected int id;
	@NonNull protected String name;

}
