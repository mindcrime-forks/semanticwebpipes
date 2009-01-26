<xsl:stylesheet 
    xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:h    ="http://www.w3.org/1999/xhtml"
    xmlns      ="http://www.w3.org/1999/xhtml"
    xmlns:DC   ="http://purl.org/dc/elements/1.1/"
    xmlns:DCTERMS = "http://purl.org/dc/terms/"
    xmlns:rdf  ="http://www.w3.org/1999/02/22-rdf-syntax-ns#">

<div>
<p>A GRDDL transformation for <cite><a
href="http://dublincore.org/documents/dcq-html/">Expressing Dublin
Core in HTML/XHTML meta and link elements</a></cite>.</p>

<address>
$Id: dc-extract.xsl,v 1.2 2009/01/26 14:17:06 fuller Exp $
</address>
</div>

<xsl:output method="xml" indent="yes"/>

<xsl:variable name="uc">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>

<xsl:variable name="lc">abcdefghijklmnopqrstuvwxyz</xsl:variable>

<xsl:template match="h:html/h:head">
 <rdf:RDF>
   <rdf:Description rdf:about="">

     <xsl:apply-templates />
   </rdf:Description>
 </rdf:RDF>
</xsl:template>


<xsl:template match='h:meta'>
  <!--
      e.g.
      <meta name="DC.title" content="First title" />
  -->

  <xsl:call-template name="item">
    <xsl:with-param name="n" select="@name" />
    <xsl:with-param name="val" select="@content" />
  </xsl:call-template>

</xsl:template>

<xsl:template match='h:link'>
  <!--
      e.g.
      <link rel="DC.relation" href="http://www.example.org/" />
  -->

  <xsl:call-template name="item">
    <xsl:with-param name="n" select="@rel" />
    <xsl:with-param name="ref" select="@href" />
  </xsl:call-template>

</xsl:template>

<xsl:template name="item">
  <xsl:param name="n" />
  <xsl:param name="val" />
  <xsl:param name="ref" />

  <xsl:variable name="ns">
    <xsl:call-template name="get-ns">
      <xsl:with-param name="n" select="$n" />
    </xsl:call-template>
  </xsl:variable>

  <xsl:if test="string-length($ns) &gt; 0">
    <xsl:variable name="ln">
      <xsl:call-template name="get-ln">
	<xsl:with-param name="n" select="$n" />
	<xsl:with-param name="ns" select="$ns" />
      </xsl:call-template>
    </xsl:variable>


    <xsl:element name="{$ln}"
		 namespace="{$ns}">
      <xsl:choose>
	<xsl:when test="$ref">
	  <rdf:Description rdf:about="{$ref}">
	    <xsl:if test="@hreflang">
	      <DC:language>
		<xsl:value-of select="@hreflang" />
	      </DC:language>
	    </xsl:if>
	  </rdf:Description>
	</xsl:when>
	
	<xsl:otherwise>
	  <xsl:if test="@xml:lang">
	    <xsl:attribute name="xml:lang">
	      <xsl:value-of select="@xml:lang" />
	    </xsl:attribute>
	  </xsl:if>

	  <xsl:if test="@scheme">
	    <xsl:variable name="dt">
	      <xsl:call-template name="get-dt">
		<xsl:with-param name="n" select="@scheme" />
	      </xsl:call-template>
	    </xsl:variable>
	    <xsl:if test="string-length($dt) &gt; 0">
	      <xsl:attribute name="rdf:datatype">
		<xsl:value-of select="$dt" />
	      </xsl:attribute>
	    </xsl:if>
	  </xsl:if>

	  <xsl:value-of select="$val" />
	</xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:if>
</xsl:template>


<xsl:template name="get-ns">
  <xsl:param name="n" />

  <xsl:variable name="pfx"
		select='substring-before(translate($n, $uc, $lc), ".")' />

  <xsl:variable name="binding"
      select='../h:link[translate(@rel, $uc, $lc) = concat("schema.", $pfx)]'/>
  <xsl:if test="$binding">
    <!--
	e.g.
	<link rel="schema.DC" href="http://purl.org/dc/elements/1.1/" />
    -->

    <!-- @@TODO: absolutize @href -->
    <xsl:variable name="ns1"
		     select='$binding/@href' />

    <xsl:variable name="ln1"
		  select='substring(
			  translate($n, $uc, $lc),
			  string-length($pfx) + 1,
			  string-length($n))' />


    <!-- per "Previous recommendations specified prefixing
	 element refinements by the element being refined,
	 for example 'DC.Date.modified' rather than
	 'DCTERMS.modified'." -->
    <xsl:variable name="ns">
      <xsl:choose>
	<xsl:when test='contains($ln1, ".")
			and $ns1 = "http://purl.org/dc/elements/1.1/"'>
	  <xsl:value-of select='"http://purl.org/dc/terms/"'/>
	</xsl:when>
      
	<xsl:otherwise><xsl:value-of select="$ns1" /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>


    <!--xsl:message>
      get-ns from <xsl:value-of select="$n" /> gives <xsl:value-of select="$ns" />
    </xsl:message-->

    <xsl:value-of select="$ns" />
  </xsl:if>
</xsl:template>


<xsl:template name="get-ln">
  <xsl:param name="n" />
  <xsl:param name="ns" />

  <xsl:variable name="ln1"
		select='substring-after(translate($n, $uc, $lc), ".") ' />

  <xsl:variable name="ln">
    <xsl:choose>
      <xsl:when test='contains($ln1, ".")
		      and $ns = "http://purl.org/dc/terms/"'>
	<xsl:value-of select='substring-after($ln1, ".") '/>
      </xsl:when>
      
      <xsl:otherwise><xsl:value-of select="$ln1" /></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

    <!--xsl:message>
      get-ln from <xsl:value-of select="$n" /> gives <xsl:value-of select="$ln" />
    </xsl:message-->
  <xsl:value-of select="$ln" />
</xsl:template>

<xsl:template name="get-dt">
  <xsl:param name="n" />

  <xsl:variable name="ns">
    <xsl:call-template name="get-ns">
      <xsl:with-param name="n" select="$n" />
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="ln" select='substring-after($n, ".")' />

  <xsl:value-of select="concat($ns, $ln)" />
</xsl:template>


<!-- don't pass text thru -->
<xsl:template match="text()|@*">
</xsl:template>


</xsl:stylesheet>
