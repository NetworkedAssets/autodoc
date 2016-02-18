/**
 * Created by Jakub on 24/11/15.
 */
function qName(source,getQualified) {
    var arr;
    var qualified;
    if (typeof source == "string") {
        qualified = source;
    } else if (typeof source == "object" && typeof source.qualified == "string") {
        qualified = source.qualified;
    }

    if (!getQualified && (typeof qualified == "string" && (arr = qualified.split(".")))) {
        return arr.pop();
    } else {
        return qualified;
    }
}