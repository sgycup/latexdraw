package net.sf.latexdraw;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import net.sf.latexdraw.instrument.CmdFXVoid;
import net.sf.latexdraw.util.BadaboomCollector;
import io.github.interacto.fsm.TimeoutTransition;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertNotEquals;

public interface HelperTest {
	static void waitForTimeoutTransitions() {
		Thread.getAllStackTraces().keySet()
			.stream()
			.filter(thread -> thread.getName().startsWith(TimeoutTransition.TIMEOUT_THREAD_NAME_BASE))
			.forEach(thread -> {
				try {
					thread.join();
				}catch(final InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			});
		WaitForAsyncUtils.waitForFxEvents();
	}

	default <T> List<T> cloneList(final List<T> list, final Function<T, T> cloner) {
		final List<T> clone = new ArrayList<>(list.size());
		list.forEach(elt -> clone.add(cloner.apply(elt)));
		return clone;
	}

	default void assertEqualsDouble(final double v1, final double v2) {
		assertThat(v2).isCloseTo(v1, within(0.0000001));
	}

	default void assertEqualsDouble(final String msg, final double v1, final double v2) {
		assertThat(v2).as(msg).isCloseTo(v1, within(0.0000001));
	}

	static String getBadaboomMessages() {
		return BadaboomCollector.INSTANCE.errorsProperty().stream().map(ex -> ex.getMessage()).collect(Collectors.joining(System.getProperty("line.separator")));
	}

	default <T extends Node> void assertNotEqualsSnapshot(final T node, final CmdFXVoid toExecBetweenSnap) {
		Bounds bounds = node.getBoundsInLocal();
		final SnapshotParameters params = new SnapshotParameters();

		final WritableImage oracle = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
		Platform.runLater(() -> node.snapshot(params, oracle));
		WaitForAsyncUtils.waitForFxEvents();

		if(toExecBetweenSnap != null) {
			toExecBetweenSnap.execute();
		}

		bounds = node.getBoundsInLocal();
		final WritableImage observ = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
		Platform.runLater(() -> node.snapshot(params, observ));
		WaitForAsyncUtils.waitForFxEvents();

		assertNotEquals("The two snapshots do not differ.", 100d, computeSnapshotSimilarity(oracle, observ), 0.001);
	}


	/**
	 * Compute the similarity of two JavaFX images.
	 * @param image1 The first image to test.
	 * @param image2 The second image to test.
	 * @return A double value in [0;100] corresponding to the similarity between the two images (pixel comparison).
	 * @throws NullPointerException If image1 or image2 is null.
	 */
	default double computeSnapshotSimilarity(final Image image1, final Image image2) {
		final int width = (int) image1.getWidth();
		final int height = (int) image1.getHeight();
		final PixelReader reader1 = image1.getPixelReader();
		final PixelReader reader2 = image2.getPixelReader();

		final double nbNonSimilarPixels = IntStream.range(0, width).parallel().mapToLong(i ->
			IntStream.range(0, height).parallel().filter(j -> reader1.getArgb(i, j) != reader2.getArgb(i, j)).count()).sum();

		return 100d - nbNonSimilarPixels / (width * height) * 100d;
	}
}
