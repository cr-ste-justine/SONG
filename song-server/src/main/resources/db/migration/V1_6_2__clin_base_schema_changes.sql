ALTER TYPE sample_type RENAME TO _sample_type;
CREATE TYPE sample_type as ENUM(
    'Total DNA',
    'Amplified DNA',
    'ctDNA',
    'Other DNA enrichments',
    'Total RNA',
    'Ribo-Zero RNA',
    'polyA+ RNA',
    'Other RNA fractions',
    'Deoxyribonucleic acid'
);
ALTER TABLE sample RENAME COLUMN type TO _type;
ALTER TABLE sample ADD COLUMN type sample_type;

UPDATE sample SET type='Total DNA' where _type='Total DNA';
UPDATE sample SET type='Amplified DNA' where _type='Amplified DNA';
UPDATE sample SET type='ctDNA' where _type='ctDNA';
UPDATE sample SET type='Other DNA enrichments' where _type='Other DNA enrichments';
UPDATE sample SET type='Total RNA' where _type='Total RNA';
UPDATE sample SET type='Ribo-Zero RNA' where _type='Ribo-Zero RNA';
UPDATE sample SET type='polyA+ RNA' where _type='polyA+ RNA';
UPDATE sample SET type='Other RNA fractions' where _type='Other RNA fractions';

ALTER TABLE sample DROP COLUMN _type;
DROP TYPE _sample_type CASCADE;

ALTER TYPE tissue_source_type RENAME TO _tissue_source_type;
CREATE TYPE tissue_source_type as ENUM(
  'Blood derived',
  'Blood derived - bone marrow',
  'Blood derived - peripheral blood',
  'Bone marrow',
  'Buccal cell',
  'Lymph node',
  'Solid tissue',
  'Plasma',
  'Serum',
  'Urine',
  'Cerebrospinal fluid',
  'Sputum',
  'Other',
  'Pleural effusion',
  'Mononuclear cells from bone marrow',
  'Saliva',
  'Skin',
  'Intestine',
  'Buffy coat',
  'Stomach',
  'Esophagus',
  'Tonsil',
  'Spleen',
  'Bone',
  'Cerebellum',
  'Endometrium',
  'Blood specimen'
);
ALTER TABLE specimen RENAME COLUMN tissue_source TO _tissue_source;
ALTER TABLE specimen ADD COLUMN tissue_source tissue_source_type;

UPDATE specimen SET tissue_source='Blood derived' where _tissue_source='Blood derived';
UPDATE specimen SET tissue_source='Blood derived - bone marrow' where _tissue_source='Blood derived - bone marrow';
UPDATE specimen SET tissue_source='Blood derived - peripheral blood' where _tissue_source='Blood derived - peripheral blood';
UPDATE specimen SET tissue_source='Bone marrow' where _tissue_source='Bone marrow';
UPDATE specimen SET tissue_source='Buccal cell' where _tissue_source='Buccal cell';
UPDATE specimen SET tissue_source='Lymph node' where _tissue_source='Lymph node';
UPDATE specimen SET tissue_source='Solid tissue' where _tissue_source='Solid tissue';
UPDATE specimen SET tissue_source='Plasma' where _tissue_source='Plasma';
UPDATE specimen SET tissue_source='Serum' where _tissue_source='Serum';
UPDATE specimen SET tissue_source='Urine' where _tissue_source='Urine';
UPDATE specimen SET tissue_source='Cerebrospinal fluid' where _tissue_source='Cerebrospinal fluid';
UPDATE specimen SET tissue_source='Sputum' where _tissue_source='Sputum';
UPDATE specimen SET tissue_source='Other' where _tissue_source='Other';
UPDATE specimen SET tissue_source='Pleural effusion' where _tissue_source='Pleural effusion';
UPDATE specimen SET tissue_source='Mononuclear cells from bone marrow' where _tissue_source='Mononuclear cells from bone marrow';
UPDATE specimen SET tissue_source='Saliva' where _tissue_source='Saliva';
UPDATE specimen SET tissue_source='Skin' where _tissue_source='Skin';
UPDATE specimen SET tissue_source='Intestine' where _tissue_source='Intestine';
UPDATE specimen SET tissue_source='Buffy coat' where _tissue_source='Buffy coat';
UPDATE specimen SET tissue_source='Stomach' where _tissue_source='Stomach';
UPDATE specimen SET tissue_source='Esophagus' where _tissue_source='Esophagus';
UPDATE specimen SET tissue_source='Tonsil' where _tissue_source='Tonsil';
UPDATE specimen SET tissue_source='Spleen' where _tissue_source='Spleen';
UPDATE specimen SET tissue_source='Bone' where _tissue_source='Bone';
UPDATE specimen SET tissue_source='Cerebellum' where _tissue_source='Cerebellum';
UPDATE specimen SET tissue_source='Endometrium' where _tissue_source='Endometrium';

ALTER TABLE specimen DROP COLUMN _tissue_source;
DROP TYPE _tissue_source_type CASCADE;