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
