<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Author</title>
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

        <h2>Author</h2>

        <div class="box">
          <div><b>ID:</b> <xsl:value-of select="author/id"/></div>
          <div><b>Full name:</b> <xsl:value-of select="author/fullName"/></div>
          <div><b>Birth year:</b> <xsl:value-of select="author/birthYear"/></div>
        </div>

        <p>
          <a>
            <xsl:attribute name="href">
              <xsl:text>/api/authors/</xsl:text>
              <xsl:value-of select="author/id"/>
              <xsl:text>/books?format=xml</xsl:text>
            </xsl:attribute>
            View books of this author
          </a>
        </p>

      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
