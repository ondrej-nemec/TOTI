package toti.application.logging;

public class Page {

	public static String section(int level, String title, String body, boolean collapsed) {
		return String.format("""
    <div class="section %s">
	    <h%s class="section-header" onclick="toggleSection(this)">
	        <span>%s</span>
	        <span class="chevron">▼</span>
	    </h%s>
	    <div class="section-content">%s</div>
	</div>
		""", collapsed ? "collapsed" : "", level, title, level, body);
	}

	public static String paragraph(String text) {
		return String.format("<div class=\"paragraph\">%s</div>", text);
	}

	public static String code(String text) {
		return String.format("<div class=\"code\">%s</div>", text);
	}

	public static String card(String text) {
		return String.format("<div class=\"card\">%s</div>", text);
	}

	public static String error(String title, String body) {
		return get("error", title, body);
	}

	public static String primary(String title, String body) {
		return get("primary", title, body);
	}

	private static String get(String level, String title, String body) {
		return String.format(
"""
<!DOCTYPE html>
<html lang="en" data-theme="light" data-page="%s">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>TOTI | %s</title>

<style>
:root {
    --bg: #ffffff;--text: #2c3e50;--muted: #6b7280;--card: #f4f6f8;--border: #e5e7eb;--code-bg: #f4f6f8;--accent: #2f80ed;
}
[data-theme="dark"] {
    --bg: #0f172a;--text: #e5e7eb;--muted: #9ca3af;--card: #111827;--border: #1f2937;--code-bg: #1e293b;
}
html[data-page="error"] { --accent: #e74c3c; }
html[data-page="success"]  { --accent: #10b981; }
html[data-page="warning"] { --accent: #f59e0b; }
body { margin: 0; font-family: system-ui, sans-serif; background: var(--bg); color: var(--text); }
.topbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    border-bottom: 1px solid var(--border);
}
.brand { font-weight: 600; color: var(--accent); }
.actions { display: flex; gap: 10px; font-size: 14px; color: var(--muted); }
.container { max-width: 900px; margin: 40px auto; padding: 0 20px; }
.card {
    background: var(--card);
    border: 1px solid var(--border);
    border-radius: 10px;
    padding: 16px;
    margin-bottom: 16px;
}
.paragraph { padding: 16px; margin-bottom: 16px; }
h1 { margin: 0 0 10px 0; color: var(--accent); }
h2 { margin: 0 0 10px 0; font-weight: normal; color: var(--muted); }
.code {
    background: var(--code-bg);
    border-left: 4px solid var(--accent);
    padding: 12px;
    font-family: monospace;
    overflow-x: auto;
}
#toggle-theme {
    background: transparent;
    color: var(--text);
    border: 1px solid var(--border);
    padding: 6px 10px;
    border-radius: 6px;
    cursor: pointer;
}
[data-theme="dark"] #theme-dark { display: none; }
[data-theme="light"] #theme-light { display: none; }
.section { border: 1px solid var(--border); border-radius: 10px; margin-bottom: 16px; overflow: hidden; }
.section-header { padding: 12px 16px; cursor: pointer; justify-content: space-between; display: flex; }
.section-header:hover { background: var(--border); }
.chevron { transition: transform 0.2s; color: var(--muted); }
.section.collapsed .chevron { transform: rotate(-90deg); }
.section-content { padding: 16px; }
.section.collapsed .section-content { display: none; }
</style>
</head>

<body>
<div class="topbar">
    <div class="brand">TOTI</div>
    <div class="actions">
        <button id="toggle-theme" onclick="toggleTheme()">
        	<span id="theme-light">☀️</span>
        	<span id="theme-dark">🌙</span>
        </button>
    </div>
</div>

<div class="container"> %s</div>

<script>
function toggleTheme() {
    const html = document.documentElement;
    const current = html.getAttribute("data-theme");
    html.setAttribute("data-theme", current === "dark" ? "light" : "dark");
}
function toggleSection(header) { header.parentElement.classList.toggle("collapsed"); }
</script>

</body>
</html>
""",
		level, title, body
		);
	}

}
