package toti.application.logging;

import java.util.HashSet;
import java.util.Set;

public class PageBuilder {

	protected static final String PRIMARY = "#2f80ed";
	protected static final String SUCCESS = "#10b981";
	protected static final String WARNING = "#f59e0b";
	protected static final String ERROR = "#e74c3c";

	private final StringBuilder style = new StringBuilder();
	private final StringBuilder script = new StringBuilder();
	private final Set<String> features = new HashSet<>();

	private void addFeature(String name, String css, String js) {
		if (features.contains(name)) {
			return;
		}
		features.add(name);
		if (css  != null) {
			style.append(css);
		}
		if (js != null) {
			script.append(js);
		}
	}

	public Block createH1() {
		return createH(1);
	}

	public Block createH2() {
		return createH(2);
	}

	public Block createH3() {
		return createH(3);
	}

	private Block createH(int level) {
		if (level > 1) {
			addFeature("h" + level, "h" + level + " { margin: 0 0 10px 0; font-weight: normal; color: var(--secondary-color); }", null);
		}
		return new Block(this, String.format("<h%s>%s</h%s>", level, "%s", level));
	}

	public Block createParagraph() {
		addFeature("p", ".paragraph { padding: 16px; margin-bottom: 16px; }", null);
		return new Block(this, "<div class=\"paragraph\">%s</div>");
	}

	public Block createCode() {
		addFeature("code", ".code { background: var(--code-bg); border-left: 4px solid var(--primary-color); padding: 12px; font-family: monospace; overflow-x: auto; }", null);
		return new Block(this, "<div class=\"code\">%s</div>");
	}

	public Block createCard() {
		addFeature("card", ".card { border-radius: 10px; padding: 16px; margin-bottom: 16px; background: var(--card); border: 1px solid var(--border); }", null);
		return new Block(this, "<div class=\"card\">%s</div>");
	}

	public Block createSection(String title, int level, boolean isCollapsed) {
		addFeature("section", """
.section { border: 1px solid var(--border); border-radius: 10px; margin-bottom: 16px; overflow: hidden; }
.section-header { padding: 12px 16px; cursor: pointer; justify-content: space-between; display: flex; }
.section-header:hover {  background: var(--border); }
.chevron {  transition: transform 0.2s; color: var(--secondary-color); }
.section.collapsed .chevron {  transform: rotate(-90deg); }
.section.collapsed .section-content {  display: none; }
""", """
function toggleSection(header) {
	const section = header.parentElement;
	section.classList.toggle("collapsed");
}
		""");
		createH(level); // for adding required css
		return new Block(this, String.format("""
<div class="section %s">
	<h%s class="section-header" onclick="toggleSection(this)">
		<span>%s</span>
		<span class="chevron">▼</span>
	</h%s>
	<div class="section-content">%s</div>
</div>
				""", isCollapsed ? "collapsed" : "", level, title, level, "%s"));
	}

	protected String getRootTemplate(String title, String primaryColor, Block body) {
		return String.format(
"""
<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>TOTI | %s</title>
<style>
html { --primary-color: %s; }
:root { --body-bg: #ffffff; --body-color: #2c3e50; --secondary-color: #6b7280; --card: #f4f6f8; --border: #e5e7eb; --code-bg: #f4f6f8; }
[data-theme="dark"] { --body-bg: #0f172a; --body-color: #e5e7eb; --secondary-color: #9ca3af; --card: #111827; --border: #1f2937; --code-bg: #1e293b; }
body { margin: 0; font-family: system-ui, sans-serif; background: var(--body-bg); color: var(--body-color); }
.header { display: flex; justify-content: space-between; align-items: center; padding: 12px 20px; border-bottom: 1px solid var(--border); height: 20px; position: fixed; top: 0; background:var(--body-bg); width: 95%; }
.brand { font-weight: 600; color: var(--primary-color); }
.container { max-width: 900px; margin: 40px auto; padding: 0 20px; }
h1 { margin: 0 0 10px 0; color: var(--primary-color); }
#toggle-theme { background: transparent; color: var(--body-color); border: 1px solid var(--border); padding: 6px 10px; border-radius: 6px; cursor: pointer; }
	[data-theme="dark"] #theme-dark { display: none; }
	[data-theme="light"] #theme-light { display: none; }
	.actions { display: flex; gap: 10px; font-size: 14px; color: var(--secondary-color); }
%s
</style>
</head>

<body>
<div class="header">
<div class="brand">TOTI - Really cool framework</div>
<div class="actions"><button id="toggle-theme" onclick="toggleTheme()"><span id="theme-light">☀️</span><span id="theme-dark">🌙</span></button></div>
</div>

<div class="container">%s</div>

<script>
document.documentElement.setAttribute("data-theme", localStorage.getItem("theme") || "light");
function toggleTheme() {
	const html = document.documentElement;
	const current = html.getAttribute("data-theme");
	var theme = current === "dark" ? "light" : "dark";
	html.setAttribute("data-theme", theme);
	localStorage.setItem("theme", theme);
}
%s
</script></body></html>
""", title, primaryColor, style.toString(), body.create(), script.toString()
		);
	}


	protected String getStyle() {
		return style.toString();
	}

	protected String getScript() {
		return script.toString();
	}

}
