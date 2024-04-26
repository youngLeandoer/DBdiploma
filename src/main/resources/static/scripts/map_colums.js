$(document).ready(function() {
    connectToDatabaseOnMapColumnsPage();
});
function connectToDatabaseOnMapColumnsPage() {
    var selectedDatabase = localStorage.getItem('selectedDatabase');
    if (selectedDatabase) {
        console.log('Connecting to database on map_columns page:', selectedDatabase);
    } else {
        console.error('No database selected.');
    }
}