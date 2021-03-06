/*
 * Copyright 1998-2009 University Corporation for Atmospheric Research/Unidata
 *
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

package ucar.nc2.iosp.hdf4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import ucar.nc2.util.DebugFlagsImpl;
import ucar.unidata.test.util.TestDir;

/**
 * Read all hdf4 files in cdmUnitTestDir + "formats/hdf4/"
 *
 * @author caron
 */
@RunWith(Parameterized.class)
public class TestH4readAll {
  static private String testDir = TestDir.cdmUnitTestDir + "formats/hdf4/";

  @AfterClass
  static public void after() {
    H4header.setDebugFlags(new DebugFlagsImpl(""));  // make sure debug flags are off
  }

  @Parameterized.Parameters(name="{0}")
 	public static Collection<Object[]> getTestParameters() throws IOException {
    Collection<Object[]> filenames = new ArrayList<>();
    TestDir.actOnAllParameterized(testDir , new H4FileFilter(), filenames);
    // TestDir.actOnAllParameterized("D:/hdf4/" , new H4FileFilter(), filenames);

 		return filenames;
 	}

  String filename;
  public TestH4readAll(String filename) {
    this.filename = filename;
  }

  @Test
  public void readAll() throws IOException {
    TestDir.readAll(filename);
  }

  static class H4FileFilter implements java.io.FileFilter {
    public boolean accept(File pathname) {
      return pathname.getName().endsWith(".hdf") || pathname.getName().endsWith(".eos") || pathname.getName().endsWith(".h4");
    }
  }

}
