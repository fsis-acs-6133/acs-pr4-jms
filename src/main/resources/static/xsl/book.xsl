<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Book</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 20px; }
          .nav a { margin-right: 12px; }
          .box { border: 1px solid #ddd; padding: 12px; border-radius: 8px; }
        </style>
      </head>
      <body>
        <div class="nav">
          <a href="/api/authors?format=xml">Authors (XML)</a>
          <a href="/api/books?format=xml">Books (XML)</a>
        </div>

        <h2>Book</h2>

        <div class="box">
          <div><b>ID:</b> <xsl:value-of select="book/id"/></div>
          <div><b>Title:</b> <xsl:value-of select="book/title"/></div>
          <div><b>Year:</b> <xsl:value-of select="book/publishedYear"/></div>
          <div><b>Author:</b>
            <xsl:choose>
              <xsl:when test="book/author/id">
                <a>
                  <xsl:attribute name="href">
                    <xsl:text>/api/authors/</xsl:text>
                    <xsl:value-of select="book/author/id"/>
                    <xsl:text>?format=xml</xsl:text>
                  </xsl:attribute>
                  <xsl:value-of select="book/author/fullName"/>
                </a>
              </xsl:when>
              <xsl:otherwise>
                <i>author deleted</i>
              </xsl:otherwise>
            </xsl:choose>
          </div>
        </div>

      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
