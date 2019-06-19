package net.sf.jkniv.sqlegance.dialect;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class SqlFeatureTest
{
    
    @Test
    public void whenCreateNewFeature()
    {
        SqlFeature feature = new SqlFeatureBase();
        assertThat(feature.supports(), is(false));
        assertThat(feature.name(), is(SqlFeatureSupport.UNKNOW.name()));
        assertThat(feature.getSqlFeature(), is(SqlFeatureSupport.UNKNOW));
        
        feature = SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT);
        assertThat(feature.supports(), is(false));
        assertThat(feature.name(), is(SqlFeatureSupport.LIMIT.name()));
        assertThat(feature.getSqlFeature(), is(SqlFeatureSupport.LIMIT));
        
        feature = SqlFeatureFactory.newInstance(SqlFeatureSupport.ROWNUM, true);        
        assertThat(feature.supports(), is(true));
        assertThat(feature.name(), is(SqlFeatureSupport.ROWNUM.name()));
        assertThat(feature.getSqlFeature(), is(SqlFeatureSupport.ROWNUM));
    }
}
