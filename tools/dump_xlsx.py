import sys
import openpyxl

sys.stdout.reconfigure(encoding='utf-8')
wb = openpyxl.load_workbook(sys.argv[1], data_only=True)
for ws in wb.worksheets:
    print('SHEET', ws.title, ws.max_row, ws.max_column)
    for row in ws.iter_rows(values_only=True):
        if any(v not in (None, '') for v in row):
            print(' | '.join('' if v is None else str(v) for v in row))
