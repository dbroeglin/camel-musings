<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
	exclude-result-prefixes="xsl fo xs fn xdt">

	<xsl:param name="TestTimestamp"/>

	<xsl:template match="/map">
		<Document timestamp="{$TestTimestamp}" 	xmlns="urn:test:csv-to-xml">
			<Field1><xsl:value-of select="entry[string[1] = 'a']/string[2]"/></Field1>
			<Field2><xsl:value-of select="entry[string[1] = 'b']/string[2]"/></Field2>
		</Document>
	</xsl:template>
</xsl:stylesheet>