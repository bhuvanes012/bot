String script =
"function getXPath(el, text) {\n" +

"  function hasText(t){ return t && t.trim() !== ''; }\n" +

"  function buildRelativePath(from, to) {\n" +
"    var path = '';\n" +
"    var current = to;\n" +

"    while (current && current !== from) {\n" +
"      var tag = current.tagName.toLowerCase();\n" +

"      var index = 1;\n" +
"      var sibling = current.previousElementSibling;\n" +

"      while (sibling) {\n" +
"        if (sibling.tagName === current.tagName) index++;\n" +
"        sibling = sibling.previousElementSibling;\n" +
"      }\n" +

"      path = '/' + tag + '[' + index + ']' + path;\n" +
"      current = current.parentElement;\n" +
"    }\n" +
"    return path;\n" +
"  }\n" +

"  // ✅ CASE 1: Element itself has ID\n" +
"  if (el.id) {\n" +
"    if (hasText(text)) {\n" +
"      return '//*[@id=\"' + el.id + '\" and contains(., \"' + text + '\")]';\n" +
"    }\n" +
"    return '//*[@id=\"' + el.id + '\"]';\n" +
"  }\n" +

"  // ✅ CASE 2: Find nearest parent with ID\n" +
"  var parent = el.parentElement;\n" +

"  while (parent && parent !== document) {\n" +
"    if (parent.id) {\n" +
"      var base = '//*[@id=\"' + parent.id + '\"]';\n" +
"      var relative = buildRelativePath(parent, el);\n" +

"      if (hasText(text)) {\n" +
"        return base + relative + '[contains(., \"' + text + '\")]';\n" +
"      }\n" +

"      return base + relative;\n" +
"    }\n" +
"    parent = parent.parentElement;\n" +
"  }\n" +

"  // ✅ CASE 3: No ID found anywhere\n" +
"  var tag = el.tagName.toLowerCase();\n" +

"  if (hasText(text)) {\n" +
"    return '//' + tag + '[contains(., \"' + text + '\")]';\n" +
"  }\n" +

"  return '//' + tag;\n" +
"}\n" +

"return getXPath(arguments[0], arguments[1]);";
