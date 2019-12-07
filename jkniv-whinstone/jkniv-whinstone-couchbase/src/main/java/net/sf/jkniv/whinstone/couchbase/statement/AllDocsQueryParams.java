/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.couchbase.statement;

/**
 * 
 * <code>
 * Query Parameters:
 *  
 *  conflicts (boolean) – Includes conflicts information in response. Ignored if include_docs isn’t true. Default is false.
 *  descending (boolean) – Return the documents in descending by key order. Default is false.
 *  endkey (string) – Stop returning records when the specified key is reached. Optional.
 *  end_key (string) – Alias for endkey param.
 *  endkey_docid (string) – Stop returning records when the specified document ID is reached. Optional.
 *  end_key_doc_id (string) – Alias for endkey_docid param.
 *  include_docs (boolean) – Include the full content of the documents in the return. Default is false.
 *  inclusive_end (boolean) – Specifies whether the specified end key should be included in the result. Default is true.
 *  key (string) – Return only documents that match the specified key. Optional.
 *  keys (string) – Return only documents that match the specified keys. Optional.
 *  limit (number) – Limit the number of the returned documents to the specified number. Optional.
 *  skip (number) – Skip this number of records before starting to return the results. Default is 0.
 *  stale (string) – Allow the results from a stale view to be used, without triggering a rebuild of all views within the encompassing design doc. Supported values: ok and update_after. Optional.
 *  startkey (string) – Return records starting with the specified key. Optional.
 *  start_key (string) – Alias for startkey param.
 *  startkey_docid (string) – Return records starting with the specified document ID. Optional.
 *  start_key_doc_id (string) – Alias for startkey_docid param.
 *  update_seq (boolean) – Response includes an update_seq value indicating which sequence id of the underlying database the view reflects. Default is false.
 *
 * </code>
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class AllDocsQueryParams
{
    /** (boolean) – Includes conflicts information in response. Ignored if include_docs isn’t true. Default is false. */
    public static final String KEY_conflicts = "conflicts";
    /** (boolean) – Return the documents in descending by key order. Default is false. */
    public static final String KEY_descending = "descending";
    /** (string) – Stop returning records when the specified key is reached. Optional. */
    public static final String KEY_endkey = "endkey";
    /** (string) – Alias for endkey param. */
    public static final String KEY_end_key = "end_key"; 
    /** (string) – Stop returning records when the specified document ID is reached. Optional. */
    public static final String KEY_endkey_docid  = "endkey_docid";
    /** (string) – Alias for endkey_docid param. */
    public static final String KEY_end_key_doc_id  = "end_key_doc_id";
    /** (boolean) – Include the full content of the documents in the return. Default is false. */
    public static final String KEY_include_docs  = "include_docs";
    /** (boolean) – Specifies whether the specified end key should be included in the result. Default is true. */
    public static final String KEY_inclusive_end  = "inclusive_end";
    /** (string) – Return only documents that match the specified key. Optional. */
    public static final String KEY_key  = "key";
    /** (string) – Return only documents that match the specified keys. Optional. */
    public static final String KEY_keys  = "keys";
    /** (number) – Limit the number of the returned documents to the specified number. Optional. */
    public static final String KEY_limit  = "limit";
    /** (number) – Skip this number of records before starting to return the results. Default is 0. */
    public static final String KEY_skip  = "skip";
    /** (string) – Allow the results from a stale view to be used, without triggering a rebuild of all views within the encompassing design doc. Supported values: ok and update_after. Optional. */
    public static final String KEY_stale  = "stale";
    /** (string) – Return records starting with the specified key. Optional. */
    public static final String KEY_startkey  = "startkey";
    /** (string) – Alias for startkey param. */
    public static final String KEY_start_key  = "start_key";
    /** (string) – Return records starting with the specified document ID. Optional. */
    public static final String KEY_startkey_docid  = "startkey_docid";
    /** (string) – Alias for startkey_docid param. */
    public static final String KEY_start_key_doc_id  = "start_key_doc_id";
    /** (boolean) – Response includes an update_seq value indicating which sequence id of the underlying database the view reflects. Default is false. */
    public static final String KEY_update_seq  = "";    
    /** Includes conflicts information in response. Ignored if include_docs isn’t true. Default is false. */
    
    /*
    private Boolean conflicts;
    // Return the documents in descending by key order. Default is false. 
    private Boolean descending;
    // Stop returning records when the specified key is reached. Optional. 
    private String endkey; 
    // Alias for endkey param. 
    private String end_key; 
    // Stop returning records when the specified document ID is reached. Optional. 
    private String endkey_docid; 
    // Alias for endkey_docid param. 
    private String end_key_doc_id;
    // Include the full content of the documents in the return. Default is false. 
    private Boolean include_docs;
    // Specifies whether the specified end key should be included in the result. Default is true. 
    private Boolean inclusive_end;
    /// Return only documents that match the specified key. Optional. 
    private String key;
    // Return only documents that match the specified keys. Optional. 
    private String keys;
    // Limit the number of the returned documents to the specified number. Optional. 
    private Long limit;
    // Skip this number of records before starting to return the results. Default is 0. 
    private Long skip; 
    // Allow the results from a stale view to be used, without triggering a rebuild of all views within the encompassing design doc. Supported values: ok and update_after. Optional. 
    private String stale;
    // Return records starting with the specified key. Optional. 
    private String startkey;
    // Alias for startkey param. 
    private String start_key;
    // Return records starting with the specified document ID. Optional. 
    private String startkey_docid; 
    // Alias for startkey_docid param. 
    private String start_key_doc_id; 
    // Response includes an update_seq value indicating which sequence id of the underlying database the view reflects. Default is false. 
    private Boolean update_seq; 
    */
}
