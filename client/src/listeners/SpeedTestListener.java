package listeners;

import java.math.BigDecimal;
import java.util.function.DoubleConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTestListener implements ISpeedTestListener {

  private static final Logger LOGGER = LogManager.getLogger(SpeedTestListener.class);

  private BigDecimal transferRateBit;
  private final DoubleConsumer onProgressCallback;
  private final Runnable onCompletionCallback;

  public SpeedTestListener(DoubleConsumer onProgressCallback, Runnable onCompletionCallback) {
    this.onProgressCallback = onProgressCallback;
    this.onCompletionCallback = onCompletionCallback;
  }

  public BigDecimal getTransferRateBit() {
    return this.transferRateBit;
  }

  @Override
  public void onCompletion(SpeedTestReport report) {
    this.transferRateBit = report.getTransferRateBit();
    LOGGER.info("Finished speed test, result: {} bit/s", this.transferRateBit);
    this.onCompletionCallback.run();
  }

  @Override
  public void onError(SpeedTestError speedTestError, String errorMessage) {
    LOGGER.error(errorMessage);
  }

  @Override
  public void onProgress(float percent, SpeedTestReport report) {
    this.onProgressCallback.accept(percent);
  }
  
}
