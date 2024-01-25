package tracker.transactionstracker.charts;

import org.icepear.echarts.Heatmap;
import org.icepear.echarts.Line;
import org.icepear.echarts.Option;
import org.icepear.echarts.charts.heatmap.HeatmapSeries;
import org.icepear.echarts.charts.line.LineAreaStyle;
import org.icepear.echarts.charts.line.LineSeries;
import org.icepear.echarts.components.coord.cartesian.CategoryAxis;
import org.icepear.echarts.components.series.SeriesLabel;
import org.icepear.echarts.render.Engine;
import org.icepear.echarts.serializer.EChartsSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tracker.transactionstracker.correlations.CorrelationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ChartService {
    private final CorrelationService correlationService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy");


    public ChartService(CorrelationService correlationService) {
        this.correlationService = correlationService;
    }

    public void generateChart() {


    }

    @GetMapping("/chart")
    public ResponseEntity<String> createHeatmapOption(Map<String, Map<String, BigDecimal>> chainDateToCorrelationMap) {
        Map<String, Map<String, BigDecimal>> correlationData = correlationService.getCorrelationMap();
        List<String> chains = new ArrayList<>(correlationData.keySet());
        Collections.sort(chains);
        List<String> dates = correlationData.values().iterator().next().keySet().stream().sorted(Comparator.comparing(range -> {
            String firstDate = range.split(" - ")[0];
            return LocalDate.parse(firstDate, FORMATTER);
        })).toList();

        List<Number[]> dataValue = new ArrayList<>();


        for (String chain : chains) {
            Map<String, BigDecimal> dateToCorrelationMap = correlationData.get(chain);
            for (String date : dates) {
                BigDecimal value = dateToCorrelationMap.get(date);
                Number correlationValue = (value != null) ? value.doubleValue() : null;
                dataValue.add(new Number[]{dates.indexOf(date), chains.indexOf(chain), correlationValue});
            }
        }
        Heatmap heatmap = new Heatmap()
                .addXAxis(dates.toArray(new String[0]))
                .addYAxis(chains.toArray(new String[0]))
                .setVisualMap(-1, 1)
                .addSeries(new HeatmapSeries().setName("Punch Card")
                        .setData(dataValue.toArray())
                        .setLabel(new SeriesLabel().setShow(true)));

        Engine engine = new Engine();
        String json = engine.renderHtml(heatmap);
        return ResponseEntity.ok(json);

    }
}
