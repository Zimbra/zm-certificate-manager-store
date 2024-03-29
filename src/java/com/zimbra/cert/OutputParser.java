/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010, 2013, 2014, 2016 Synacor, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cert;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

public class OutputParser {
    private static final String ERROR_PREFIX = "XXXXX ERROR:";
    private static final Pattern GET_CERT_OUT_PATTERN = Pattern.compile("^([^=]+)=(.*)$");

    // parse the output of the zmcertmgr cmd
    public static HashMap<String, String> parseOuput(byte[] in) throws IOException, ServiceException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(in)));
        String line;
        HashMap<String, String> hash = new HashMap();
        Matcher matcher;
        String key;
        String value;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("STARTCMD:") || line.startsWith("ENDCMD:")) {
                continue;
            } else if (line.startsWith(ERROR_PREFIX)) {
                throw ServiceException.FAILURE(line, null);
            } else {
                ZimbraLog.security.debug("DEBUG: Current Line = " + line);
                // line = line.replaceFirst(OUTPUT_PREFIX, "").trim(); //remove the OUTPUT_PREFIX
                // for GetCert
                matcher = GET_CERT_OUT_PATTERN.matcher(line);
                if (matcher.matches()) {
                    key = matcher.group(1);
                    value = matcher.group(2);
                    // System.out.println("Key = " + key + "; value="+ value);
                    hash.put(key, value);
                } else {
                    continue;
                }
            }
        }

        return hash;
    }

    // parse the output of the zmcertmgr cmd
    public static String cleanCSROutput(byte[] in) throws IOException, ServiceException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(in)));
        String line;
        StringBuilder csrContent = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("STARTCMD:") || line.startsWith("ENDCMD:")) {
                continue;
            } else if (line.startsWith(ERROR_PREFIX)) {
                throw ServiceException.FAILURE(line, null);
            } else {
                csrContent.append(line).append("\n");
            }
        }
        return csrContent.toString();
    }

    // Example:
    // subject=C = US, ST = NY, L = Buffalo, O = "Synacor, Inc.", OU = Zimbra Collaboration Suite, CN = admindev.zimbra.com

    public static HashMap<String, String> parseSubject(String subject) {
        HashMap<String, String> hash = new HashMap<String, String>();
        // this will cause issue when the subject contains /
        // String [] dsn = subject.split("/");
        Matcher matcher;
        String key;
        String value;
        Pattern key_pattern = Pattern.compile("^\\s?(C|ST|L|O|OU|CN)\\s=\\s(.*),?$");
        Pattern value_pattern = Pattern.compile("^(.*?)(\\s?(C|ST|L|O|OU|CN)\\s=\\s.*)$");
        String parsing_literal = subject.trim();
        matcher = key_pattern.matcher(parsing_literal);
        while (matcher.matches()) {
            key = matcher.group(1);
            parsing_literal = matcher.group(2);
            matcher = value_pattern.matcher(parsing_literal);

            if (matcher.matches()) {
                value = matcher.group(1);
                parsing_literal = matcher.group(2);
            } else {
                value = parsing_literal;
            }
            value = value.replaceFirst(",?$", "");
            hash.put(key, value);
            matcher = key_pattern.matcher(parsing_literal);
        }
        return hash;
    }

    // SubjectAltNames=DNS:admindev.zimbra.com, DNS:test1.zimbra.com, DNS:test2.zimbra.com
    public static Vector<String> parseSubjectAltName(String subjectAltNames) {
        // ZimbraLog.security.info(subjectAltNames);
        Vector<String> vec = new Vector<String>();
        String[] dns = subjectAltNames.split(",");
        for (int i = 0; i < dns.length; i++) {
            vec.add(dns[i].trim());
        }
        /*
         * zmcertmgr remove the DNS.* already Matcher matcher; String value; for (int i=0; i < dns.length; i++) { matcher = GET_SUBJECT_ALT_NAME_PATTERN.matcher(dns[i]); if (matcher.matches()) { value
         * = matcher.group(1); //ZimbraLog.security.info("Host " + i + " = " + value); vec.add(value); } }
         */
        return vec;
    }

    // parse verification result
    public static boolean parseVerifyResult(byte[] in) throws IOException, ServiceException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(in)));
        String line;
        String ERROR_CERT_OUTPUT = "error:";
        while ((line = br.readLine()) != null) {
            if (line.startsWith("STARTCMD:") || line.startsWith("ENDCMD:")) {
                continue;
            } else if (line.startsWith(ERROR_PREFIX) || line.contains(ERROR_PREFIX) || line.contains(ERROR_CERT_OUTPUT)) {
                // throw ServiceException.FAILURE(line, null);
                return false;
            }
        }

        return true;
    }
}