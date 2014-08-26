package ucar.nc2.ft.point;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import ucar.ma2.Array;
import ucar.ma2.StructureData;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.ft.*;
import ucar.nc2.time.CalendarDate;
import ucar.nc2.time.CalendarDateUnit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RunWith(JMockit.class)  // Not sure if this is strictly necessary.
public class PointDatasetIteratorTest {
    @Test
    public void calcBounds() throws URISyntaxException, IOException, NoFactoryFoundException {
        try (   FeatureDatasetPoint orthogonalFdPoint       = newFeatureDatasetPoint("orthogonal.ncml");
                FeatureDatasetPoint contiguousRaggedFdPoint = newFeatureDatasetPoint("continuousRagged.ncml");
                FeatureDatasetPoint indexRaggedFdPoint      = newFeatureDatasetPoint("indexRagged.ncml")) {
            final List<FeatureCollection> featCols = new LinkedList<>();
            featCols.addAll(orthogonalFdPoint.getPointFeatureCollectionList());
            featCols.addAll(contiguousRaggedFdPoint.getPointFeatureCollectionList());
            featCols.addAll(indexRaggedFdPoint.getPointFeatureCollectionList());
            Assert.assertEquals(3, featCols.size());

            // Create a fake FeatureDatasetPoint that returns our desired FeatureCollections.
            // See http://jmockit.github.io/tutorial/StateBasedTesting.html#interfaces
            FeatureDatasetPoint fakeFdPoint = new MockUp<FeatureDatasetPoint>() {
                @Mock
                public List<FeatureCollection> getPointFeatureCollectionList() {
                    return featCols;
                }
            }.getMockInstance();

            PointDatasetIterator pointIter = new PointDatasetIterator(fakeFdPoint);
            pointIter.setCalculateBounds(null);

            List<Float> expectedHumidityVals = Arrays.asList(1f, 2f, 3f, 10f, 20f, 30f, 100f, 200f, 300f);
            List<Float> actualHumidityVals = getMemberValsFromIter(pointIter, "humidity");
            Assert.assertEquals(expectedHumidityVals, actualHumidityVals);

            Assert.assertEquals(9, pointIter.getCount());

            Assert.assertEquals(-85,  pointIter.getBoundingBox().getLatMin(), 0);
            Assert.assertEquals(+85,  pointIter.getBoundingBox().getLatMax(), 0);
            Assert.assertEquals(-70,  pointIter.getBoundingBox().getLonMin(), 0);
            Assert.assertEquals(+170, pointIter.getBoundingBox().getLonMax(), 0);

            CalendarDateUnit calDateUnit = CalendarDateUnit.of("standard", "days since 1970-01-01 00:00:00");
            CalendarDate expectedStart = calDateUnit.makeCalendarDate(90);
            CalendarDate expectedEnd   = calDateUnit.makeCalendarDate(400);

            Assert.assertEquals(expectedStart.getMillis(), pointIter.getCalendarDateRange().getStart().getMillis());
            Assert.assertEquals(expectedEnd.getMillis(),   pointIter.getCalendarDateRange().getEnd()  .getMillis());
        }
    }

    private static FeatureDatasetPoint newFeatureDatasetPoint(String resource)
            throws IOException, NoFactoryFoundException, URISyntaxException {
        File file = new File(PointDatasetIteratorTest.class.getResource(resource).toURI());
        return (FeatureDatasetPoint) FeatureDatasetFactoryManager.open(
                FeatureType.ANY_POINT, file.getAbsolutePath(), null);
    }

    private static List<Float> getMemberValsFromIter(PointFeatureIterator pointIter, String memberName)
            throws IOException {
        List<Float> memberVals = new LinkedList<>();
        try {
            while (pointIter.hasNext()) {
                PointFeature pointFeat = pointIter.next();
                StructureData data = pointFeat.getData();

                Array memberArray = data.getArray(memberName);
                Assert.assertEquals(1, memberArray.getSize());
                memberVals.add(memberArray.getFloat(0));
            }
        } finally {
            pointIter.finish();
        }

        return memberVals;
    }
}
