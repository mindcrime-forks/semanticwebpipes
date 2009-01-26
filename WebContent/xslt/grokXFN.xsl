<xsl:stylesheet 
    xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xfn  ="http://gmpg.org/xfn/11#"
    xmlns:foaf ="http://xmlns.com/foaf/0.1/"
    xmlns:h    ="http://www.w3.org/1999/xhtml"
    xmlns:rdf  ="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    >

<!--
  $Id: grokXFN.xsl,v 1.2 2009/01/26 14:17:06 fuller Exp $
  interpret XFN as RDF

  We take the liberty of treating link relationship names
  from the http://gmpg.org/xfn/11 profile as RDF property
  names in the http://gmpg.org/xfn/11# namespace,
  since they're used as ids there. This
  follows grokXMDP.xsl as well.
  -->

<xsl:output method="xml" indent="yes"/>

<xsl:template match="h:html">
  <rdf:RDF>
    <rdf:Description>
      <!-- about who/what? -->
      <foaf:homepage rdf:resource=''/> <!-- @@hmm... -->

      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"contact"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"acquaintance"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"friend"'/>
      </xsl:call-template>

      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"met"'/>
      </xsl:call-template>

      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"co-worker"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"colleague"'/>
      </xsl:call-template>

      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"co-resident"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"neighbor"'/>
      </xsl:call-template>

      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"child"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"parent"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"sibling"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"spouse"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"kin"'/>
      </xsl:call-template>

      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"muse"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"crush"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"date"'/>
      </xsl:call-template>
      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"sweetheart"'/>
      </xsl:call-template>

      <xsl:call-template name='typedLinks'>
	<xsl:with-param name='rel' select='"me"'/>
      </xsl:call-template>

    </rdf:Description>
  </rdf:RDF>
</xsl:template>

<xsl:template name='typedLinks'>
  <xsl:param name='rel'/>

  <xsl:for-each select=".//h:a[contains(concat(' ', @rel, ' '),
			concat(' ', $rel, ' '))]">
    <xsl:element name='{$rel}' namespace='http://gmpg.org/xfn/11#'>
      <rdf:Description>
	<foaf:homepage rdf:resource="{@href}"/>
      </rdf:Description>
    </xsl:element>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
