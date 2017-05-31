package org.robinbird;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.robinbird.listener.Java8Listener;
import org.robinbird.parser.Java8Lexer;
import org.robinbird.parser.Java8Parser;
import org.robinbird.utils.AppArguments;
import org.robinbird.utils.Msgs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.robinbird.utils.Msgs.Key.IOEXCEPTION_WHILE_READING_SOURCE_CODES;

/**
 * Created by seokhyun on 5/26/17.
 */
@Slf4j
public class Application {

	public void run(AppArguments appArgs) {
		log.info("Start app with args: " + appArgs.toString());
		try (Stream<Path> paths = Files.walk(Paths.get(appArgs.getSourceRootPath()))) {
			paths.filter(p -> {
					if (!Files.isRegularFile(p)) { return false; }
					if (!p.getFileName().toString().contains("java")) { return false; }
					return true;
			}).forEach(p -> parseFile(p));
		} catch (IOException|UncheckedIOException e) {
			log.debug("Skip current java file because of exception: " + Msgs.get(IOEXCEPTION_WHILE_READING_SOURCE_CODES));
		}
	}

	public void parseFile(Path p) {
		log.debug("Parse file: " + p.getFileName());
		try {
			ANTLRInputStream input = new ANTLRInputStream(new String(Files.readAllBytes(p)));

			Java8Lexer lexer = new Java8Lexer(input);
			Java8Parser parser = new Java8Parser(new CommonTokenStream(lexer));
			Java8Parser.CompilationUnitContext tree = parser.compilationUnit();
			Java8Listener listener = new Java8Listener();

			ParseTreeWalker.DEFAULT.walk(listener, tree);
		} catch (IOException e) {
			log.debug("Skip current java file because of exception: " + Msgs.get(IOEXCEPTION_WHILE_READING_SOURCE_CODES));
		}
	}

	public static void main(String[] args) {
		Application app = new Application();
		AppArguments appArgs = AppArguments.parseArguments(args);
		app.run(appArgs);
	}
}
