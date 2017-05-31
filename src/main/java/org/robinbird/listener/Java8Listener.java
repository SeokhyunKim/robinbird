package org.robinbird.listener;

import org.robinbird.parser.Java8Parser;

/**
 * Created by seokhyun on 5/26/17.
 */
public class Java8Listener extends org.robinbird.parser.Java8BaseListener {

	@Override
	public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
		System.out.println(ctx.Identifier().getText());
	}

	@Override
	public void enterNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
		System.out.println(ctx.Identifier().getText());
	}

}
