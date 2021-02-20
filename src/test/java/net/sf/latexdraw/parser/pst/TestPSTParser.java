package net.sf.latexdraw.parser.pst;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import net.sf.latexdraw.LatexdrawExtension;
import net.sf.latexdraw.model.api.shape.Shape;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(LatexdrawExtension.class)
public abstract class TestPSTParser {
	PSTLatexdrawListener listener;
	List<Shape> parsedShapes;
	Handler parserLogHandler;

	@BeforeEach
	void setUp() {
		parserLogHandler = new Handler() {
			@Override
			public void publish(final LogRecord record) {
				fail(record.getMessage());
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		};
		listener = new ErrorPSTLatexdrawListener();
		listener.log.addHandler(parserLogHandler);
	}

	<T extends Shape> T getShapeAt(final int i) {
		return (T) parsedShapes.get(i);
	}

	void parser(final String code) {
		final net.sf.latexdraw.parser.pst.PSTLexer lexer = new net.sf.latexdraw.parser.pst.PSTLexer(CharStreams.fromString(code));
		final net.sf.latexdraw.parser.pst.PSTParser parser = new net.sf.latexdraw.parser.pst.PSTParser(new CommonTokenStream(lexer));
		parser.addParseListener(listener);
		final ErrorListener errList = new ErrorListener();
		parser.addErrorListener(errList);
		parser.pstCode(new PSTContext());
		parsedShapes = listener.flatShapes();
		parser.removeParseListener(listener);
		parser.removeErrorListener(errList);
		parser.getInterpreter().clearDFA();
		lexer.getInterpreter().clearDFA();
		new ATNDeserializer().deserialize(net.sf.latexdraw.parser.pst.PSTLexer._serializedATN.toCharArray());
	}

	public static class ErrorListener extends BaseErrorListener {
		@Override
		public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine,
			final String msg, final RecognitionException e) {
			fail(msg);
		}
	}

	public static class ErrorPSTLatexdrawListener extends PSTLatexdrawListener {
		public ErrorPSTLatexdrawListener() {
			super();
		}

		@Override
		public void enterUnknowncmds(final net.sf.latexdraw.parser.pst.PSTParser.UnknowncmdsContext ctx) {
			super.enterUnknowncmds(ctx);
			fail(ctx.LATEXCMD().getText());
		}

		@Override
		public void enterUnknownParamSetting(final net.sf.latexdraw.parser.pst.PSTParser.UnknownParamSettingContext ctx) {
			super.enterUnknownParamSetting(ctx);
			fail(ctx.name.getText());
		}
	}
}
