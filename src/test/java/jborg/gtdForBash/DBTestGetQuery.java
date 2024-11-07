package jborg.gtdForBash;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import jborg.gtdForBash.DBIssues.DBSink;

public class DBTestGetQuery
{

	@Test
	void test() throws SQLException
	{
		
		DBSink db = new DBSink();
		int id = db.getProjectIDByName("SoziSchulden");
		assert(id==8);
	}

}
