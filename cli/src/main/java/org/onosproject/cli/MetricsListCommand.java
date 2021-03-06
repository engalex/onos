/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.cli;

import static java.lang.String.format;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.karaf.shell.commands.Command;
import org.joda.time.LocalDateTime;
import org.onlab.metrics.MetricsService;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

/**
 * Prints metrics in the system.
 */
@Command(scope = "onos", name = "metrics",
         description = "Prints metrics in the system")
public class MetricsListCommand extends AbstractShellCommand {

    @Override
    protected void execute() {
        MetricsService metricsService = get(MetricsService.class);

        MetricFilter filter = MetricFilter.ALL;

        TreeMultimap<String, Metric> matched = listMetrics(metricsService, filter);
        matched.asMap().forEach((name, metrics) -> {
            for (Metric metric : metrics) {
                printMetric(name, metric);
            }
        });
    }

    /**
     * Print metric object.
     *
     * @param name metric name
     * @param metric metric object
     */
    private void printMetric(String name, Metric metric) {
        final String heading;

        if (metric instanceof Counter) {
            heading = format("-- %s : [%s] --", name, "Counter");
            print(heading);
            Counter counter = (Counter) metric;
            print("          count = %d", counter.getCount());

        } else if (metric instanceof Gauge) {
            heading = format("-- %s : [%s] --", name, "Gauge");
            print(heading);
            @SuppressWarnings("rawtypes")
            Gauge gauge = (Gauge) metric;
            final Object value = gauge.getValue();
            if (name.endsWith("EpochMs") && value instanceof Long) {
                print("          value = %s (%s)", value, new LocalDateTime(value));
            } else {
                print("          value = %s", value);
            }

        } else if (metric instanceof Histogram) {
            heading = format("-- %s : [%s] --", name, "Histogram");
            print(heading);
            final Histogram histogram = (Histogram) metric;
            final Snapshot snapshot = histogram.getSnapshot();
            print("          count = %d", histogram.getCount());
            print("            min = %d", snapshot.getMin());
            print("            max = %d", snapshot.getMax());
            print("           mean = %f", snapshot.getMean());
            print("         stddev = %f", snapshot.getStdDev());

        } else if (metric instanceof Meter) {
            heading = format("-- %s : [%s] --", name, "Meter");
            print(heading);
            final Meter meter = (Meter) metric;
            print("          count = %d", meter.getCount());
            print("      mean rate = %f", meter.getMeanRate());
            print("  1-minute rate = %f", meter.getOneMinuteRate());
            print("  5-minute rate = %f", meter.getFiveMinuteRate());
            print(" 15-minute rate = %f", meter.getFifteenMinuteRate());

        } else if (metric instanceof Timer) {
            heading = format("-- %s : [%s] --", name, "Timer");
            print(heading);
            final Timer timer = (Timer) metric;
            final Snapshot snapshot = timer.getSnapshot();
            print("          count = %d", timer.getCount());
            print("      mean rate = %f", timer.getMeanRate());
            print("  1-minute rate = %f", timer.getOneMinuteRate());
            print("  5-minute rate = %f", timer.getFiveMinuteRate());
            print(" 15-minute rate = %f", timer.getFifteenMinuteRate());
            print("            min = %f ms", nanoToMs(snapshot.getMin()));
            print("            max = %f ms", nanoToMs(snapshot.getMax()));
            print("           mean = %f ms", nanoToMs(snapshot.getMean()));
            print("         stddev = %f ms", nanoToMs(snapshot.getStdDev()));
        } else {
            heading = format("-- %s : [%s] --", name, metric.getClass().getCanonicalName());
            print(heading);
            print("Unknown Metric type:{}", metric.getClass().getCanonicalName());
        }
        print(Strings.repeat("-", heading.length()));
    }

    @SuppressWarnings("rawtypes")
    private TreeMultimap<String, Metric> listMetrics(MetricsService metricsService, MetricFilter filter) {
        TreeMultimap<String, Metric> metrics = TreeMultimap.create(Comparator.naturalOrder(), Ordering.arbitrary());

        Map<String, Counter> counters = metricsService.getCounters(filter);
        for (Entry<String, Counter> entry : counters.entrySet()) {
            metrics.put(entry.getKey(), entry.getValue());
        }
        Map<String, Gauge> gauges = metricsService.getGauges(filter);
        for (Entry<String, Gauge> entry : gauges.entrySet()) {
            metrics.put(entry.getKey(), entry.getValue());
        }
        Map<String, Histogram> histograms = metricsService.getHistograms(filter);
        for (Entry<String, Histogram> entry : histograms.entrySet()) {
            metrics.put(entry.getKey(), entry.getValue());
        }
        Map<String, Meter> meters = metricsService.getMeters(filter);
        for (Entry<String, Meter> entry : meters.entrySet()) {
            metrics.put(entry.getKey(), entry.getValue());
        }
        Map<String, Timer> timers = metricsService.getTimers(filter);
        for (Entry<String, Timer> entry : timers.entrySet()) {
            metrics.put(entry.getKey(), entry.getValue());
        }

        return metrics;
    }

    private double nanoToMs(double nano) {
        return nano / 1_000_000D;
    }
}
