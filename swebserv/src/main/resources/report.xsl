<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
		<body>
			<h1>swebserv</h1>
			<h2>Simulation results</h2>
			<p><xsl:value-of select="swebserv/date"/></p>
			<ul>
				<li>Mean request service time = 
				<xsl:value-of select="swebserv/meanReqServiceTime"/></li>
				<li>Max server request queue length = 
				<xsl:value-of select="swebserv/maxReqQueueLength"/></li>
				<li>Number of requests = 
				<xsl:value-of select="swebserv/nReq"/></li>
				<li>PRNGs seed = 
				<xsl:value-of select="swebserv/PRNGseed"/></li>
			</ul>
			<p><img src="meanWaitTimeVsX.png"></img></p>
			<p><img src="dropFractionVsX.png"></img></p>
			<table border="1">
				<tr bgcolor="#9acd32">
					<th>meanReqArrivalTime</th>
					<th>meanWaitTime</th>
					<th>waitTimeStddev</th>
					<th>reqDropFraction</th>
				</tr>
				<xsl:for-each select="swebserv/simulations/simulation">
				<tr>
					<td><xsl:value-of select="@meanReqArrivalTime"/></td>
					<td><xsl:value-of select="@meanWaitTime"/></td>
					<td><xsl:value-of select="@waitTimeStddev"/></td>
					<td><xsl:value-of select="@reqDropFraction"/></td>
				</tr>
				</xsl:for-each>
				<tr>
					
				</tr>
			</table>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>