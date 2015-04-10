package org.tdar.search.index.field;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapted from
 * Roberto Bicchierai rbicchierai@open-lab.com
 * Pietro Polsinelli ppolsinelli@open-lab.com
 * for the Teamwork Project Management application - http://www.twproject.com
 * Open Lab - Florence - Italy
 * Released under LGPL - use it as you want
 * 
 * This is a lazy and minimal implementation for Lucene Fieldable
 */
public class LazyReaderField extends Field {

    private Reader reader;
    private final static transient Logger logger = LoggerFactory.getLogger(LazyReaderField.class);

    public LazyReaderField(String name, Reader reader, Float boost) {
        super(name, reader, TextField.TYPE_NOT_STORED);
        this.reader = reader;
        // fundamental set: this instructs Lucene not to call the stringValue on field creation, but only when needed
        // super.lazy = true;
        if (boost != null) {
            setBoost(boost);
        }
    }

    @Override
    public Reader readerValue() {
        return reader;
    }

    @Override
    protected void finalize() throws Throwable {
        if (reader != null) {
            logger.trace("closing reader");
            IOUtils.closeQuietly(reader);
        }
        super.finalize();
    }

    @Override
    public TokenStream tokenStream(Analyzer analyzer, TokenStream reuse) throws IOException {
        return super.tokenStream(analyzer, reuse);
    }

    public Reader getReader() {
        return reader;
    }

}
