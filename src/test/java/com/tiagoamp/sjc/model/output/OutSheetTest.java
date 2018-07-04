package com.tiagoamp.sjc.model.output;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.SjcSpecificCode;

public class OutSheetTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testSortRows() {
		// given
		OutSheet sheet = new OutSheet(SjcSpecificCode.OPERACIONAL_PLANTOESEXTRA);
		List<OutRow> rows = Arrays.asList(
					new OutRow("lotacao 01", "nome 01", "001"), // 0
					new OutRow("lotacao 01", "nome 02", "002"), // 1 - repeated
					new OutRow("lotacao 01", "nome 03", "003"), // 2
					new OutRow("lotacao 02", "nome 04", "004"), // 3
					new OutRow("lotacao 02", "nome 05", "005"), // 4
					new OutRow("lotacao 03", "nome 02", "002"), // 5 - repeated
					new OutRow("lotacao 03", "nome 07", "007"), // 6
					new OutRow("lotacao 04", "nome 02", "002")  // 7 - repeated
				);
		sheet.setRows(rows);
		int initialRepeatedIndex = 1;
		int numOfRepetitions = 3;
		int prevSize = rows.size();
		// when
		sheet.sortRows();
		//then
		assertEquals("List should have same size after sorting.", prevSize, sheet.getRows().size());
		List<OutRow> outRows = sheet.getRows();		
		for (int i = initialRepeatedIndex; i < numOfRepetitions-1; i++) {
			OutRow currRow = outRows.get(i);
			OutRow nextRow = outRows.get(i+1);
			assertEquals("Repeated elements should be grouped.", currRow.getMatricula(), nextRow.getMatricula());
			assertTrue("Same person should be marked as duplicated row", currRow.hasDuplicates() && nextRow.hasDuplicates());
		}
		outRows.forEach(System.out::println);
	}

}
