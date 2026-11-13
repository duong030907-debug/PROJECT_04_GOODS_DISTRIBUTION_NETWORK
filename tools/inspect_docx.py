import sys, io
from docx import Document
from docx.table import Table
from docx.text.paragraph import Paragraph

sys.stdout.reconfigure(encoding='utf-8')

path = sys.argv[1]
d = Document(path)
print('PARAS:', len(d.paragraphs), 'TABLES:', len(d.tables), 'SECTIONS:', len(d.sections))
body = d.element.body
pi = 0
ti = 0
for child in body.iterchildren():
    tag = child.tag.split('}')[-1]
    if tag == 'p':
        p = Paragraph(child, d)
        txt = p.text.strip()
        print(f'P{pi:<3} [{p.style.name}] {txt[:300] if txt else "<empty>"}')
        pi += 1
    elif tag == 'tbl':
        t = Table(child, d)
        print(f'--- TABLE {ti} rows={len(t.rows)} cols={len(t.columns)}')
        for ri, row in enumerate(t.rows):
            cells = [c.text.strip().replace('\n', ' / ')[:60] for c in row.cells]
            print(f'    R{ri}: ' + ' || '.join(cells))
        ti += 1
    elif tag == 'sectPr':
        print('--- SECTPR ---')
    else:
        print(f'--- {tag} ---')
