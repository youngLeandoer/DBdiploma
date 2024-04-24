function getDatabaseList() {
    fetch('/api/database/list')
        .then(response => response.json())
        .then(data => {
            const databaseList = document.getElementById('databaseList');
            databaseList.innerHTML = '';
            data.forEach(database => {
                const listItem = document.createElement('li');
                listItem.textContent = database;
                databaseList.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error fetching database list:', error));
}

function connectToDatabase() {
    var selectedDatabase = $('#selectedDatabase').val();
    $.ajax({
        url: 'http://localhost:8080/api/database/connect?database=' + selectedDatabase,
        type: 'POST',
        success: function(response) {
            console.log('Connected to database:', selectedDatabase);

            $('#mappingButton').prop('disabled', false);

            const showDataButton = document.createElement('button');
            showDataButton.textContent = 'Show Imported Data';
            showDataButton.classList.add('btn', 'btn-primary', 'mt-3');
            showDataButton.onclick = showImportedData;
            document.querySelector('.container').appendChild(showDataButton);

            const alert = document.createElement('div');
            alert.classList.add('alert', 'alert-success', 'mt-3');
            alert.textContent = 'База данных успешно подключена!';
            document.querySelector('.container').appendChild(alert);

            setTimeout(() => {
                alert.remove();
            }, 3000);
        },
        error: function(xhr, status, error) {
            console.error('Error connecting to database:', error);
        }
    });
}

function showImportedData() {
    var selectedDatabase = $('#selectedDatabase').val();
    $.ajax({
        url: '/api/database/imported-data',
        type: 'GET',
        data: { database: selectedDatabase },
        success: function(response) {
            console.log('Received imported data:', response);

            $('#importedDataContainer').empty();

            var table = $('<table>').addClass('table');
            var thead = $('<thead>').appendTo(table);
            var tbody = $('<tbody>').appendTo(table);

            var headerRow = $('<tr>').appendTo(thead);
            if (response.length > 0) {
                Object.keys(response[0]).forEach(function(key) {
                    $('<th>').text(key).appendTo(headerRow);
                });

                response.forEach(function(rowData) {
                    var row = $('<tr>').appendTo(tbody);
                    Object.values(rowData).forEach(function(value) {
                        $('<td>').text(value).appendTo(row);
                    });
                });
            } else {
                $('<th>').text('id').appendTo(headerRow);
                $('<th>').text('name').appendTo(headerRow);
                $('<th>').text('value').appendTo(headerRow);

                var emptyRow = $('<tr>').appendTo(tbody);
                $('<td colspan="3">').text('No data available').appendTo(emptyRow);
            }

            $('#importedDataContainer').append(table);
        },
        error: function(xhr, status, error) {
            console.error('Error fetching imported data:', error);
        }
    });
}



$('#themeSwitcher').on('click', function() {
    $('body').toggleClass('dark-theme');
    if ($('body').hasClass('dark-theme')) {
        $('.styled-table').addClass('table-dark');
    } else {
        $('.styled-table').removeClass('table-dark');
    }
});

function goBack() {
    window.history.back();
}

