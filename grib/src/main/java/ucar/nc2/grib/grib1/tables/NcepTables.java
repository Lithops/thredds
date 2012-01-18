/*
 * Copyright (c) 1998 - 2012. University Corporation for Atmospheric Research/Unidata
 * Portions of this software were developed by the Unidata Program at the
 * University Corporation for Atmospheric Research.
 *
 * Access and use of this software shall impose the following obligations
 * and understandings on the user. The user is granted the right, without
 * any fee or cost, to use, copy, modify, alter, enhance and distribute
 * this software, and any derivative works thereof, and its supporting
 * documentation for any purpose whatsoever, provided that this entire
 * notice appears in all copies of the software, derivative works and
 * supporting documentation.  Further, UCAR requests that the user credit
 * UCAR/Unidata in any publications that result from the use of this
 * software or in any product that includes this software. The names UCAR
 * and/or Unidata, however, may not be used in any advertising or publicity
 * to endorse or promote any products or commercial entity unless specific
 * written permission is obtained from UCAR/Unidata. The user also
 * understands that UCAR/Unidata is not obligated to provide the user with
 * any support, consulting, training or assistance of any kind with regard
 * to the use, operation and performance of this software nor to provide
 * the user with any updates, revisions, new versions or "bug fixes."
 *
 * THIS SOFTWARE IS PROVIDED BY UCAR/UNIDATA "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL UCAR/UNIDATA BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING
 * FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION
 * WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ucar.nc2.grib.grib1.tables;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import ucar.grib.GribResourceReader;
import ucar.nc2.grib.GribLevelType;
import ucar.nc2.grib.GribStatType;
import ucar.nc2.grib.VertCoord;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * NCEP overrides of GRIB tables
 *
 * @author caron
 * @since 1/13/12
 */
public class NcepTables extends Grib1Customizer {
  static private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NcepTables.class);

  private static HashMap<Integer, String> genProcessMap;  // shared by all instances
  private static HashMap<Integer, GribLevelType> levelTypesMap;  // shared by all instances

  NcepTables(Grib1ParamTables tables) {
    super(7, tables);
  }

  protected NcepTables(int center, Grib1ParamTables tables) {
    super(center, tables);
  }

  // http://www.nco.ncep.noaa.gov/pmb/docs/on388/table5.html
  @Override
  public GribStatType getStatType(int timeRangeIndicator) {
    if (timeRangeIndicator < 128) return super.getStatType(timeRangeIndicator);

    switch (timeRangeIndicator) {
      case 128:
      case 129:
      case 130:
      case 131:
      case 132:
      case 133:
      case 137:
      case 138:
      case 139:
      case 140:
        return GribStatType.Average;
      case 134:
        return GribStatType.RootMeanSquare;
      case 135:
      case 136:
        return GribStatType.StandardDeviation;
      default:
        return null;
    }
  }

  @Override
  public String getTimeTypeName(int timeRangeIndicator) {
    if (timeRangeIndicator < 128) return super.getTimeTypeName(timeRangeIndicator);

    switch (timeRangeIndicator) {
      case 128:
        /* Average of forecast accumulations. P1 = start of accumulation period. P2 = end of accumulation period.
          Reference time is the start time of the first forecast, other forecasts at 24-hour intervals.
          Number in Ave = number of forecasts used. */
        return "Average of forecast accumulations at 24 hour intervals, period = (RT + P1) to (RT + P2)";

      case 129:
        /* Average of successive forecast accumulations. P1 = start of accumulation period.
        P2 = end of accumulation period. Reference time is the start time of the first forecast,
        other forecasts at (P2 - P1) intervals. Number in Ave = number of forecasts used */
        return "Average of successive forecast accumulations, period = (RT + P1) to (RT + P2)";

      case 130:
        /* Average of forecast averages. P1 = start of averaging period. P2 = end of averaging period.
        Reference time is the start time of the first forecast, other forecasts at 24-hour intervals.
        Number in Ave = number of forecast used*/
        return "Average of forecast averages at 24 hour intervals, period = (RT + P1) to (RT + P2)";

      case 131:
        /* Average of successive forecast averages. P1 = start of averaging period. P2 = end of averaging period.
        Reference time is the start time of the first forecast, other forecasts at (P2 - P1) intervals.
        Number in Ave = number of forecasts used*/
        return "Average of successive forecast averages, period = (RT + P1) to (RT + P2)";

      case 132:
        return "Climatological Average of N analyses, each a year apart, starting from initial time R and for the period from R+P1 to R+P2.";

      case 133:
        return "Climatological Average of N forecasts, each a year apart, starting from initial time R and for the period from R+P1 to R+P2.";

      case 134:
        return "Climatological Root Mean Square difference between N forecasts and their verifying analyses, each a year apart, starting with initial time R and for the period from R+P1 to R+P2.";

      case 135:
        return "Climatological Standard Deviation of N forecasts from the mean of the same N forecasts, for forecasts one year apart. ";

      case 136:
        return "Climatological Standard Deviation of N analyses from the mean of the same N analyses, for analyses one year apart.";

      case 137:
        /* Average of forecast accumulations. P1 = start of accumulation period. P2 = end of accumulation period.
        Reference time is the start time of the first forecast, other forecasts at 6-hour intervals.
        Number in Ave = number of forecast used */
        return "Average of forecast accumulations at 6 hour intervals, period = (RT + P1) to (RT + P2)";

      case 138:
        /* Average of forecast averages. P1 = start of averaging period. P2 = end of averaging period.
        Reference time is the start time of the first forecast, other forecasts at 6-hour intervals.
        Number in Ave = number of forecast used */
        return "Average of forecast averages at 6 hour intervals, period = (RT + P1) to (RT + P2)";

      case 139:
        /* Average of forecast accumulations. P1 = start of accumulation period. P2 = end of accumulation period.
        Reference time is the start time of the first forecast, other forecasts at 12-hour intervals.
        Number in Ave = number of forecast used */
        return "Average of forecast accumulations at 12 hour intervals, period = (RT + P1) to (RT + P2)";

      case 140:
        /* Average of forecast averages. P1 = start of averaging period. P2 = end of averaging period.
        Reference time is the start time of the first forecast, other forecasts at 12-hour intervals.
        Number in Ave = number of forecast used */
        return "Average of forecast averages at 12 hour intervals, period = (RT + P1) to (RT + P2)";

      default:
        return null;
    }
  }

  // genProcess

  @Override
  public String getTypeGenProcessName(int genProcess) {
    if (genProcessMap == null) readNcepGenProcess("resources/grib1/ncep/ncepTableA.xml");
    if (genProcessMap == null) return null;
    return genProcessMap.get(genProcess);
  }

  private void readNcepGenProcess(String path) {
    InputStream is = null;
    try {
      is = GribResourceReader.getInputStream(path);
      if (is == null) {
        logger.error("Cant find NCEP Table 1 = " + path);
        return;
      }

      SAXBuilder builder = new SAXBuilder();
      org.jdom.Document doc = builder.build(is);
      Element root = doc.getRootElement();

      HashMap<Integer, String> result = new HashMap<Integer, String>(200);
      List<Element> params = root.getChildren("parameter");
      for (Element elem1 : params) {
        int code = Integer.parseInt(elem1.getAttributeValue("code"));
        String desc = elem1.getChildText("description");
        result.put(code, desc);
      }

      genProcessMap = result;  // all at once - thread safe
      return;

    } catch (IOException ioe) {
      logger.error("Cant read NCEP Table 1 = " + path, ioe);
      return;

    } catch (JDOMException e) {
      logger.error("Cant parse NCEP Table 1 = " + path, e);
      return;

    } finally {
      if (is != null) try {
        is.close();
      } catch (IOException e) {
      }
    }
  }

  /// levels

  @Override
  protected VertCoord.VertUnit makeVertUnit(int code) {
    GribLevelType lt = getLevelType(code);
    return (lt != null) ? lt : super.makeVertUnit(code);
  }

  @Override
  public String getLevelNameShort(int code) {
    GribLevelType lt = getLevelType(code);
    return (lt == null) ? super.getLevelNameShort(code) : lt.getAbbrev();
  }

  @Override
  public String getLevelDescription(int code) {
    GribLevelType lt = getLevelType(code);
    return (lt == null) ? super.getLevelDescription(code) : lt.getDesc();
  }

  @Override
  public String getLevelUnits(int code) {
    GribLevelType lt = getLevelType(code);
    return (lt == null) ? super.getLevelUnits(code) : lt.getUnits();
  }

  @Override
  public boolean isLayer(int code) {
    GribLevelType lt = getLevelType(code);
    return (lt == null) ? super.isLayer(code) : lt.isLayer();
  }

  @Override
  public boolean isPositiveUp(int code) {
    GribLevelType lt = getLevelType(code);
    return (lt == null) ? super.isPositiveUp(code) : lt.isPositiveUp();
  }

  @Override
  public String getLevelDatum(int code) {
    GribLevelType lt = getLevelType(code);
    return (lt == null) ? super.getLevelDatum(code) : lt.getDatum();
  }

  private GribLevelType getLevelType(int code) {
    if (code < 129)
      return null; // LOOK dont let NCEP override standard tables (??) looks like a conflict with level code 210 (!)
    if (levelTypesMap == null)
      levelTypesMap = readTable3("resources/grib1/ncep/ncepTable3.xml");
    if (levelTypesMap == null)
      return null;

    return levelTypesMap.get(code);
  }


}
