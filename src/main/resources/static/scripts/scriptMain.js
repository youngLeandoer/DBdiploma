let tableData = [];

$('#loadDataBtn').on('click', function() {
    var excelFile = $('#excelFile')[0].files[0];
    if (!excelFile) {
        alert('Пожалуйста, выберите файл Excel.');
        return;
    }

    var reader = new FileReader();
    reader.onload = function(e) {
        var data = new Uint8Array(e.target.result);
        var workbook = XLSX.read(data, { type: 'array' });
        var sheetName = workbook.SheetNames[0];
        var sheet = workbook.Sheets[sheetName];
        var excelData = XLSX.utils.sheet_to_json(sheet, { header: 1 });
        tableData = excelData;
        renderTable();
    };
    reader.readAsArrayBuffer(excelFile);
});

function renderTable() {
    var tableHtml = '<table class="styled-table table table-bordered">';
    tableData.forEach(function(row) {
        tableHtml += '<tr>';
        row.forEach(function(cell) {
            tableHtml += '<td class="editable-cell">' + cell + '</td>';
        });
        tableHtml += '<td><button class="btn btn-sm btn-danger delete-row-btn" onclick="deleteRow(this)">Удалить строку</button></td>';
        tableHtml += '</tr>';
    });
    tableHtml += '<tr><td colspan="' + tableData[0].length + '"><button class="btn btn-sm btn-success add-row-btn" onclick="addRow()">Добавить строку</button></td></tr>';
    tableHtml += '<tr>';
    for (var i = 0; i < tableData[0].length; i++) {
        tableHtml += '<td><button class="btn btn-sm btn-danger delete-col-btn" onclick="deleteColumn(' + i + ')">Удалить столбец</button></td>';
    }
    tableHtml += '<td><button class="btn btn-sm btn-success add-col-btn" onclick="addColumn()">Добавить столбец</button></td>';
    tableHtml += '</tr>';
    tableHtml += '</table>';
    $('#tableContainer').html(tableHtml);
    makeCellsEditable();
}

function makeCellsEditable() {
    $('.editable-cell').on('click', function() {
        var $cell = $(this);
        var currentValue = $cell.text().trim();
        var $input = $('<input>', { type: 'text', class: 'form-control', value: currentValue });
        $cell.empty().append($input);
        $input.focus();
        $input.on('blur', function() {
            var newValue = $(this).val().trim();
            $cell.text(newValue);
            updateTableData();
        });
    });
}

function addRow() {
    var newRow = [];
    for (var i = 0; i < tableData[0].length; i++) {
        newRow.push('');
    }
    tableData.push(newRow);
    renderTable();
}

function addColumn() {
    tableData.forEach(function(row) {
        row.push('');
    });
    renderTable();
}

function deleteRow(button) {
    var rowIndex = $(button).closest('tr').index();
    tableData.splice(rowIndex, 1);
    renderTable();
}

function deleteColumn(index) {
    tableData.forEach(function(row) {
        row.splice(index, 1);
    });
    renderTable();
}

function updateTableData() {
    var updatedTableData = [];
    $('.styled-table tr').each(function() {
        var rowData = [];
        $(this).find('td').each(function() {
            rowData.push($(this).text().trim());
        });
        updatedTableData.push(rowData);
    });
    tableData = updatedTableData;
}
function saveTableData() {
    // Собираем данные таблицы
    const rows = document.querySelectorAll('#tableContainer tr');
    const data = [];

    rows.forEach(row => {
        const rowData = [];
        row.querySelectorAll('td').forEach(cell => {
            // Проверяем, что элемент не кнопка
            if (!cell.querySelector('button')) {
                rowData.push(cell.textContent);
            }
        });
        data.push(rowData);
    });

    // Создаем новый Excel-файл
    var wb = XLSX.utils.book_new();
    var ws = XLSX.utils.aoa_to_sheet(data);
    XLSX.utils.book_append_sheet(wb, ws, "Sheet1");

    // Сохраняем файл
    XLSX.writeFile(wb, 'table_data.xlsx');

    // Выводим сообщение об успешном сохранении
    const alert = document.createElement('div');
    alert.classList.add('alert', 'alert-success', 'mt-3');
    alert.textContent = 'Данные успешно сохранены!';
    document.querySelector('.container').appendChild(alert);

    setTimeout(() => {
        alert.remove();
    }, 3000);
}
document.getElementById('saveDataBtn').addEventListener('click', saveTableData);

$('#themeSwitcher').on('click', function() {
    $('body').toggleClass('dark-theme');
    if ($('body').hasClass('dark-theme')) {
        $('.styled-table').addClass('table-dark');
    } else {
        $('.styled-table').removeClass('table-dark');
    }
});