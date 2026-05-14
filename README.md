

public static List<String> buildUniqueXpaths(WebDriver driver,
                                             WebElement element,
                                             String text) {

    JavascriptExecutor js = (JavascriptExecutor) driver;

    return (List<String>) js.executeScript(

        "var el = arguments[0];" +
        "var txt = arguments[1];" +
        "var result = [];" +

        // =========================
        // add only unique xpath
        // =========================
        "function addXpath(xp) {" +
        "   if(!xp || xp.trim() === '') return;" +

        "   try {" +
        "       var nodes = document.evaluate(" +
        "           xp," +
        "           document," +
        "           null," +
        "           XPathResult.ORDERED_NODE_SNAPSHOT_TYPE," +
        "           null" +
        "       );" +

        "       if(nodes.snapshotLength === 1 && result.indexOf(xp) === -1) {" +
        "           result.push(xp);" +
        "       }" +
        "   } catch(e) {}" +
        "}" +

        // =========================
        // get index among same tag siblings
        // =========================
        "function getIndex(node) {" +
        "   var index = 1;" +
        "   var sib = node.previousElementSibling;" +

        "   while(sib) {" +
        "       if(sib.tagName === node.tagName) {" +
        "           index++;" +
        "       }" +
        "       sib = sib.previousElementSibling;" +
        "   }" +

        "   return index;" +
        "}" +

        "var tag = el.tagName.toLowerCase();" +
        "var idx = getIndex(el);" +

        // =========================
        // 1. Element ID
        // =========================
        "if(el.id && el.id.trim() !== '') {" +
        "   addXpath('//*[@id=\"' + el.id + '\"]');" +
        "}" +

        // =========================
        // 2. Parent / Grandparent / Upper IDs
        // =========================
        "var current = el.parentElement;" +
        "var level = 1;" +

        "while(current && level <= 10) {" +

        "   if(current.id && current.id.trim() !== '') {" +

        "       var base = '//*[@id=\"' + current.id + '\"]';" +

        // text based
        "       if(txt && txt.trim() !== '') {" +
        "           addXpath(base + '//' + tag + '[contains(text(),\"' + txt + '\")]');" +
        "           addXpath(base + '//*[contains(text(),\"' + txt + '\")]');" +
        "       }" +

        // tag based
        "       addXpath(base + '//' + tag);" +

        // indexed xpath
        "       addXpath(base + '(//' + tag + ')[' + idx + ']');" +

        // direct child indexed
        "       addXpath(base + '//' + tag + '[' + idx + ']');" +
        "   }" +

        "   current = current.parentElement;" +
        "   level++;" +
        "}" +

        // =========================
        // 3. Attribute based
        // =========================
        "var attrs = ['name','type','placeholder','value','title','aria-label','class'];" +

        "for(var i=0; i<attrs.length; i++) {" +

        "   var attr = attrs[i];" +
        "   var val = el.getAttribute(attr);" +

        "   if(val && val.trim() !== '') {" +

        "       addXpath('//' + tag + '[@' + attr + '=\"' + val + '\"]');" +

        "       if(val.indexOf(' ') > -1) {" +
        "           var first = val.split(' ')[0];" +
        "           addXpath('//' + tag + '[contains(@' + attr + ',\"' + first + '\")]');" +
        "       }" +
        "   }" +
        "}" +

        // =========================
        // 4. Text based fallback
        // =========================
        "if(txt && txt.trim() !== '') {" +
        "   addXpath('//' + tag + '[contains(text(),\"' + txt + '\")]');" +
        "}" +

        // =========================
        // 5. Absolute index fallback
        // =========================
        "var all = document.getElementsByTagName(tag);" +

        "for(var j=0; j<all.length; j++) {" +
        "   if(all[j] === el) {" +
        "       addXpath('(//' + tag + ')[' + (j + 1) + ']');" +
        "       break;" +
        "   }" +
        "}" +

        // return top 5
        "return result.slice(0,5);",

        element,
        text
    );
}



public static List<String> buildUniqueXpaths(WebDriver driver,
                                             WebElement element,
                                             String text) {

    JavascriptExecutor js = (JavascriptExecutor) driver;

    return (List<String>) js.executeScript(

        "var el = arguments[0];" +
        "var txt = arguments[1];" +
        "var result = [];" +

        // add only unique xpath
        "function addXpath(xp) {" +
        "   if(!xp || xp.trim() === '') return;" +

        "   try {" +
        "       var nodes = document.evaluate(" +
        "           xp," +
        "           document," +
        "           null," +
        "           XPathResult.ORDERED_NODE_SNAPSHOT_TYPE," +
        "           null" +
        "       );" +

        // only add if xpath returns single unique element
        "       if(nodes.snapshotLength === 1 && result.indexOf(xp) === -1) {" +
        "           result.push(xp);" +
        "       }" +
        "   } catch(e) {}" +
        "}" +

        "var tag = el.tagName.toLowerCase();" +

        // =========================
        // 1. Element ID
        // =========================
        "if(el.id && el.id.trim() !== '') {" +
        "   addXpath('//*[@id=\"' + el.id + '\"]');" +
        "}" +

        // =========================
        // 2. Parent IDs
        // =========================
        "var current = el.parentElement;" +
        "var level = 0;" +

        "while(current && level < 10) {" +

        "   if(current.id && current.id.trim() !== '') {" +

        "       var base = '//*[@id=\"' + current.id + '\"]';" +

        // text xpath
        "       if(txt && txt.trim() !== '') {" +
        "           addXpath(base + '//*[contains(text(),\"' + txt + '\")]');" +
        "           addXpath(base + '//' + tag + '[contains(text(),\"' + txt + '\")]');" +
        "       }" +

        // direct tag
        "       addXpath(base + '//' + tag);" +
        "   }" +

        "   current = current.parentElement;" +
        "   level++;" +
        "}" +

        // =========================
        // 3. Attribute based
        // =========================
        "var attrs = ['name','type','placeholder','value','title','aria-label','class'];" +

        "for(var i=0; i<attrs.length; i++) {" +

        "   var attr = attrs[i];" +
        "   var val = el.getAttribute(attr);" +

        "   if(val && val.trim() !== '') {" +

        "       addXpath('//' + tag + '[@' + attr + '=\"' + val + '\"]');" +

        "       if(val.indexOf(' ') > -1) {" +
        "           var firstClass = val.split(' ')[0];" +
        "           addXpath('//' + tag + '[contains(@' + attr + ',\"' + firstClass + '\")]');" +
        "       }" +
        "   }" +
        "}" +

        // =========================
        // 4. Text based
        // =========================
        "if(txt && txt.trim() !== '') {" +
        "   addXpath('//' + tag + '[contains(text(),\"' + txt + '\")]');" +
        "   addXpath('//*[contains(text(),\"' + txt + '\")]');" +
        "}" +

        // =========================
        // 5. Positional fallback
        // =========================
        "var siblings = document.getElementsByTagName(tag);" +

        "for(var j=0; j<siblings.length; j++) {" +
        "   if(siblings[j] === el) {" +
        "       addXpath('(//' + tag + ')[' + (j + 1) + ']');" +
        "       break;" +
        "   }" +
        "}" +

        // return top 5 unique xpaths
        "return result.slice(0,5);",

        element,
        text
    );
}







public static List<String> buildXpaths(WebDriver driver, WebElement element, String text) {

    JavascriptExecutor js = (JavascriptExecutor) driver;

    return (List<String>) js.executeScript(

        "var el = arguments[0];" +
        "var txt = arguments[1];" +
        "var list = [];" +

        "var tag = el.tagName.toLowerCase();" +

        // =========================
        // 1. Element ID xpath
        // =========================
        "if(el.id && el.id.trim() !== '') {" +
        "   list.push('//*[@id=\"' + el.id + '\"]');" +
        "}" +

        // =========================
        // 2. Parent hierarchy IDs
        // =========================
        "var current = el.parentElement;" +
        "var level = 0;" +

        "while(current && level < 5) {" +

        "   if(current.id && current.id.trim() !== '') {" +

        "       var baseXpath = '//*[@id=\"' + current.id + '\"]';" +

        // using text
        "       if(txt && txt.trim() !== '') {" +
        "           list.push(baseXpath + '//*[contains(text(),\"' + txt + '\")]');" +
        "           list.push(baseXpath + '//*[@text=\"' + txt + '\"]');" +
        "       }" +

        // using tag
        "       list.push(baseXpath + '//' + tag);" +
        "   }" +

        "   current = current.parentElement;" +
        "   level++;" +
        "}" +

        // =========================
        // 3. Attribute based xpath
        // =========================
        "if(list.length === 0 || (el.id == null || el.id.trim() === '')) {" +

        "   var attrs = ['name','type','class','placeholder','value','title','aria-label'];" +

        "   for(var i=0; i<attrs.length; i++) {" +

        "       var attr = attrs[i];" +
        "       var val = el.getAttribute(attr);" +

        "       if(val && val.trim() !== '') {" +
        "           list.push('//' + tag + '[@' + attr + '=\"' + val + '\"]');" +
        "       }" +
        "   }" +
        "}" +

        // =========================
        // 4. Text fallback
        // =========================
        "if(txt && txt.trim() !== '') {" +
        "   list.push('//*[contains(text(),\"' + txt + '\")]');" +
        "}" +

        // =========================
        // 5. Final fallback
        // =========================
        "list.push('//' + tag);" +

        // remove duplicates
        "list = [...new Set(list)];" +

        // return top 5
        "return list.slice(0,5);",

        element,
        text
    );
}





public static List<String> buildXpaths(WebDriver driver, WebElement element, String text) {

    JavascriptExecutor js = (JavascriptExecutor) driver;

    return (List<String>) js.executeScript(

        "var el = arguments[0];" +
        "var txt = arguments[1];" +
        "var list = [];" +

        "var current = el.parentElement;" +
        "var level = 0;" +

        // collect 5 xpath candidates
        "while(current && level < 5) {" +

        "   if(current.id && current.id.trim() !== '') {" +

        "       var baseXpath = '//*[@id=\"' + current.id + '\"]';" +

        "       if(txt && txt.trim() !== '') {" +
        "           list.push(baseXpath + '//*[contains(text(),\"' + txt + '\")]');" +
        "           list.push(baseXpath + '//*[@text=\"' + txt + '\"]');" +
        "       }" +

        "       var tag = el.tagName.toLowerCase();" +
        "       list.push(baseXpath + '//' + tag);" +
        "   }" +

        "   current = current.parentElement;" +
        "   level++;" +
        "}" +

        // fallback
        "if(list.length === 0) {" +
        "   var tag = el.tagName.toLowerCase();" +

        "   if(txt && txt.trim() !== '') {" +
        "       list.push('//*[contains(text(),\"' + txt + '\")]');" +
        "   }" +

        "   list.push('//' + tag);" +
        "}" +

        // remove duplicates
        "list = [...new Set(list)];" +

        // return max 5
        "return list.slice(0,5);",

        element,
        text
    );
}
