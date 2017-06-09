package org.robinbird.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.robinbird.listener.Java8Listener;
import org.robinbird.parser.java8.Java8Lexer;
import org.robinbird.parser.java8.Java8Parser;
import org.robinbird.utils.Msgs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.robinbird.model.AnalysisUnit.Language.JAVA8;
import static org.robinbird.utils.Msgs.Key.IOEXCEPTION_WHILE_READING_SOURCE_CODES;

/**
 * Created by seokhyun on 6/3/17.
 */
@Slf4j
@Getter
public class AnalysisUnit {

	public enum Language { JAVA8 }

	private Language language = JAVA8;

	private List<Path> paths = new ArrayList<>();
	private Repository<Type> types = new Repository<>();
	private Repository<Class> classes = new Repository<>();

	public AnalysisUnit(Language language) {
		this.language = language;
	}

	public void addPath(Path p) {
		paths.add(p);
	}

	public AnalysisContext analysis() {
		AnalysisContext analysisContext = new AnalysisContext(types, classes);
		for (Path path : paths) {
			log.info("Start analysis for " + path);
			if (Files.isRegularFile(path)) {
				analysisFile(path, analysisContext);
				continue;
			}
			try (Stream<Path> paths = Files.walk(path)) {
				paths.filter(p -> {
					if (!Files.isRegularFile(p)) {
						return false;
					}
					if (!p.getFileName().toString().contains("java")) {
						return false;
					}
					return true;
				}).forEach(p -> analysisFile(p, analysisContext));
			} catch (IOException | UncheckedIOException e) {
				log.debug("Skip current java file because of exception: " + Msgs.get(IOEXCEPTION_WHILE_READING_SOURCE_CODES, e));
			}
		}
		return analysisContext;
	}

	private void analysisFile(Path p, AnalysisContext analysisContext) {
		log.debug("Parse file: " + p.getFileName());
		String fileBytes = null;
		try {
			fileBytes = new String(Files.readAllBytes(p));
			ParseTree parseTree = getParseTree(fileBytes);
			ParseTreeListener listener = getParseTreeListener(analysisContext);
			ParseTreeWalker.DEFAULT.walk(listener, parseTree);
		} catch (IOException e) {
			log.debug("Skip current java file because of exception: " + Msgs.get(IOEXCEPTION_WHILE_READING_SOURCE_CODES, e));
		}
	}

	private ParseTree getParseTree(String fileBytes) {
		ANTLRInputStream input = new ANTLRInputStream(fileBytes);
		ParseTree tree;
		switch(language) {
			case JAVA8:
			default:
				Java8Lexer lexer = new Java8Lexer(input);
				Java8Parser parser = new Java8Parser(new CommonTokenStream(lexer));
				tree = parser.compilationUnit();
				break;
		}
		return tree;
	}

	private ParseTreeListener getParseTreeListener(AnalysisContext analysisContext) {
		ParseTreeListener listener;
		switch(language) {
			case JAVA8:
			default:
				Java8Listener java8Listener = new Java8Listener();
				java8Listener.setAnalysisContext(analysisContext);;
				listener = java8Listener;
				break;
		}
		return listener;
	}
}
