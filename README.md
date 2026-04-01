String script =
"function getXPath(el, text) {\n" +
"  function hasText(t){ return t && t.trim() !== ''; }\n" +
"  function getIndex(node) {\n" +
"    var i = 1;\n" +
"    var sib = node.previousElementSibling;\n" +
"    while (sib) {\n" +
"      if (sib.tagName === node.tagName) i++;\n" +
"      sib = sib.previousElementSibling;\n" +
"    }\n" +
"    return i;\n" +
"  }\n" +
"  function esc(t){ return t.replace(/\"/g, '\\\\\"'); }\n" +
"  var tag = el.tagName.toLowerCase();\n" +
"  if (el.id) {\n" +
"    if (hasText(text)) {\n" +
"      return '//' + tag + '[@id=\"' + el.id + '\" and contains(normalize-space(.), \"' + esc(text) + '\")]';\n" +
"    }\n" +
"    return '//' + tag + '[@id=\"' + el.id + '\"]';\n" +
"  }\n" +
"  var parent = el.parentElement;\n" +
"  while (parent && parent !== document) {\n" +
"    if (parent.id) {\n" +
"      var base = '//*[@id=\"' + parent.id + '\"]';\n" +
"      if (hasText(text)) {\n" +
"        return base + '//' + tag + '[contains(normalize-space(.), \"' + esc(text) + '\")]';\n" +
"      }\n" +
"      return base + '//' + tag + '[' + getIndex(el) + ']';\n" +
"    }\n" +
"    parent = parent.parentElement;\n" +
"  }\n" +
"  if (hasText(text)) {\n" +
"    return '//' + tag + '[contains(normalize-space(.), \"' + esc(text) + '\")]';\n" +
"  }\n" +
"  return '//' + tag + '[' + getIndex(el) + ']';\n" +
"}\n" +
"return getXPath(arguments[0], arguments[1]);";
