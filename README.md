

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
