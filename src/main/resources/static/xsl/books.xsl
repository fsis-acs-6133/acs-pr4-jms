<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Books</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 20px; }
          table { border-collapse: collapse; width: 100%; }
          th, td { border: 1px solid #ddd; padding: 8px; }
          th { background: #f2f2f2; }
          .nav a { margin-right: 12px; }
        </style>
      </head>
      <body>
        <div class="nav">
          <a href="/api/authors?format=xml">Authors (XML)</a>
          <a href="/api/books?format=xml">Books (XML)</a>
        </div>

        <h2>Books</h2>
        <table>
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Year</th>
            <th>Author</th>
          </tr>

          <xsl:for-each select="books/book">
            <tr>
              <td><xsl:value-of select="id"/></td>
              <td>
                <a>
                  <xsl:attribute name="href">
                    <xsl:text>/api/books/</xsl:text>
                    <xsl:value-of select="id"/>
                    <xsl:text>?format=xml</xsl:text>
                  </xsl:attribute>
                  <xsl:value-of select="title"/>
                </a>
              </td>
              <td><xsl:value-of select="publishedYear"/></td>
              <td>
                <xsl:choose>
                  <xsl:when test="author/id">
                    <a>
                      <xsl:attribute name="href">
                        <xsl:text>/api/authors/</xsl:text>
                        <xsl:value-of select="author/id"/>
                        <xsl:text>?format=xml</xsl:text>
                      </xsl:attribute>
                      <xsl:value-of select="author/fullName"/>
                    </a>
                  </xsl:when>
                  <xsl:otherwise>
                    <i>author deleted</i>
                  </xsl:otherwise>
                </xsl:choose>
              </td>
            </tr>
          </xsl:for-each>
        </table>

      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
