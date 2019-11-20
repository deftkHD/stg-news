var bg_color = 'rgb(0, 0, 0)';
var primary_text_color = 'rgb(255, 255, 255)';
var headline_color = 'rgb(255, 255, 255)';
var link_color = 'rgb(176, 59, 59)';
var figcaption_color = 'rgb(119, 127, 136)';
var ol_color = 'rgb(176, 59, 59)';
var h3_color = 'rgb(180, 48, 48)';
var h4_color = 'rgb(140, 48, 48)';

document.getElementsByTagName('body')[0].style.background = bg_color;
document.getElementById('content').style.background = bg_color;

var textElements = document.getElementsByTagName('p');
for (i = 0; i < textElements.length; i++) {
    textElements[i].style.color = primary_text_color;
}

var textElements = document.getElementsByTagName('a');
var dummy = document.createElement('dummy');
dummy.innerHTML = textElements[3].innerHTML;
textElements[3].parentNode.replaceChild(dummy, textElements[3]);
var dummy = document.createElement('dummy');
dummy.innerHTML = textElements[3].innerHTML;
textElements[3].parentNode.replaceChild(dummy, textElements[3]);

var textElements = document.getElementsByTagName('a');
for(i = 0; i < textElements.length; i++) {
    textElements[i].style.color = link_color;
}

var textElements = document.getElementsByTagName('figcaption');
for(i = 0; i < textElements.length; i++) {
    textElements[i].style.color = figcaption_color;
}

var textElements = document.getElementsByTagName('ol');
for(i = 0; i < textElements.length; i++) {
    textElements[i].style.color = ol_color;
}

var textElements = document.getElementsByTagName('h3');
for(i = 0; i < textElements.length; i++) {
    textElements[i].style.color = h3_color;
}

var textElements = document.getElementsByTagName('h4');
for(i = 0; i < textElements.length; i++) {
    textElements[i].style.color = h4_color;
}

document.getElementsByClassName('entry-title')[0].style.color = headline_color;