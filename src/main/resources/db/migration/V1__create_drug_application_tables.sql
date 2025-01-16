CREATE TABLE IF NOT EXISTS drug_application (
                                                application_number VARCHAR(50) PRIMARY KEY
    );

CREATE TABLE IF NOT EXISTS drug_application_manufacturer (
                                                             id SERIAL PRIMARY KEY,
                                                             application_number VARCHAR(50) NOT NULL REFERENCES drug_application(application_number) ON DELETE CASCADE,
    manufacturer_name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS drug_application_substance (
                                                          id SERIAL PRIMARY KEY,
                                                          application_number VARCHAR(50) NOT NULL REFERENCES drug_application(application_number) ON DELETE CASCADE,
    substance_name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS drug_application_product (
                                                        id SERIAL PRIMARY KEY,
                                                        application_number VARCHAR(50) NOT NULL REFERENCES drug_application(application_number) ON DELETE CASCADE,
    product_number VARCHAR(255) NOT NULL
    );

CREATE UNIQUE INDEX IF NOT EXISTS idx_drug_application_application_number
    ON drug_application (application_number);

CREATE UNIQUE INDEX IF NOT EXISTS idx_drug_application_manufacturer_unique
    ON drug_application_manufacturer (application_number, manufacturer_name);

CREATE UNIQUE INDEX IF NOT EXISTS idx_drug_application_substance_unique
    ON drug_application_substance (application_number, substance_name);

CREATE UNIQUE INDEX IF NOT EXISTS idx_drug_application_product_unique
    ON drug_application_product (application_number, product_number);

CREATE INDEX IF NOT EXISTS idx_drug_application_manufacturer_app_number
    ON drug_application_manufacturer (application_number);

CREATE INDEX IF NOT EXISTS idx_drug_application_manufacturer_name
    ON drug_application_manufacturer (manufacturer_name);

CREATE INDEX IF NOT EXISTS idx_drug_application_substance_app_number
    ON drug_application_substance (application_number);

CREATE INDEX IF NOT EXISTS idx_drug_application_substance_name
    ON drug_application_substance (substance_name);

CREATE INDEX IF NOT EXISTS idx_drug_application_product_app_number
    ON drug_application_product (application_number);

CREATE INDEX IF NOT EXISTS idx_drug_application_product_number
    ON drug_application_product (product_number);
