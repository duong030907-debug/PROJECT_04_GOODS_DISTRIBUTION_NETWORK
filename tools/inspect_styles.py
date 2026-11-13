import sys
from docx import Document
from docx.shared import Pt

sys.stdout.reconfigure(encoding='utf-8')
d = Document(sys.argv[1])

print('=== SECTION SETUP ===')
for i, s in enumerate(d.sections):
    print(i, 'page', s.page_width, s.page_height, 'margins L/R/T/B',
          s.left_margin, s.right_margin, s.top_margin, s.bottom_margin)
    print('   header_dist', s.header_distance, 'footer_dist', s.footer_distance,
          'different_first_page', s.different_first_page_header_footer)
    for name, part in (('header', s.header), ('footer', s.footer)):
        print('   ', name, 'is_linked', part.is_linked_to_previous)
        for p in part.paragraphs:
            if p.text.strip():
                print('      ', repr(p.text[:120]), [ (r.text, r.font.name, r.font.size) for r in p.runs][:6])
    print('   start_type', s.start_type)

print('\n=== STYLES ===')
for st in d.styles:
    try:
        f = st.font
        print(f'{st.name!r:28} type={st.type} base={st.base_style.name if st.base_style else None} '
              f'font={f.name} size={f.size} bold={f.bold} italic={f.italic} color={f.color.rgb if f.color and f.color.type else None} '
              f'space_before={st.paragraph_format.space_before} after={st.paragraph_format.space_after} '
              f'line={st.paragraph_format.line_spacing} align={st.paragraph_format.alignment} keep_next={st.paragraph_format.keep_with_next}')
    except Exception as e:
        print(st.name, 'ERR', e)

print('\n=== FIRST PARAGRAPHS DETAIL ===')
for i, p in enumerate(d.paragraphs[:35]):
    pf = p.paragraph_format
    print(i, '|', p.style.name, '| align', pf.alignment, 'sb', pf.space_before, 'sa', pf.space_after,
          'ind', pf.left_indent, pf.first_line_indent, '| runs:', [(r.text[:40], r.bold, r.font.size, r.font.name) for r in p.runs][:5])
