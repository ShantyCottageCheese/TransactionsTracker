package tracker.transactionstracker.charts;

import org.icepear.echarts.Heatmap;
import org.icepear.echarts.charts.heatmap.HeatmapSeries;
import org.icepear.echarts.components.coord.cartesian.CategoryAxis;
import org.icepear.echarts.components.series.SeriesLabel;
import org.icepear.echarts.render.Engine;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.correlations.CorrelationData;
import tracker.transactionstracker.correlations.CorrelationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChartService {
    private final CorrelationService correlationService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy");

    public ChartService(CorrelationService correlationService) {
        this.correlationService = correlationService;
    }

    public String generateHeatmap(int days) {
        Map<String, CorrelationData> correlationData = correlationService.getCorrelationForPeriods(days);
        List<String> chains = new ArrayList<>(correlationData.keySet());
        Collections.sort(chains);
        if (chains.isEmpty()) {
            return "N/A";
        }
        List<String> dates = correlationData.values().iterator().next().getDateRangeWithCorrelation().keySet().stream().sorted(Comparator.comparing(range -> {
            String firstDate = range.split(" - ")[0];
            return LocalDate.parse(firstDate, FORMATTER);
        })).toList();

        List<Number[]> dataValue = new ArrayList<>();

        for (int i = 0; i < chains.size(); i++) {
            String chain = chains.get(i);
            CorrelationData dateToCorrelationMap = correlationData.get(chain);

            for (int j = 0; j < dates.size(); j++) {
                String date = dates.get(j);
                BigDecimal value = dateToCorrelationMap.getDateRangeWithCorrelation().get(date);

                Number correlationValue = (value != null) ? value.doubleValue() : null;
                dataValue.add(new Number[]{j, i, correlationValue});
            }
        }

        Heatmap heatmap = new Heatmap()
                .addXAxis(new CategoryAxis().setName("Date").setData(dates.toArray(new String[0])))
                .addYAxis(new CategoryAxis().setName("Blockchain").setData(chains.toArray(new String[0])))
                .setTitle("Heatmap Correlation")
                .setVisualMap(-1, 1)
                .addSeries(new HeatmapSeries()
                        .setData(dataValue.toArray())
                        .setLabel(new SeriesLabel().setShow(true)));
        Engine engine = new Engine();
        return engine.renderHtml(heatmap);
    }
}
