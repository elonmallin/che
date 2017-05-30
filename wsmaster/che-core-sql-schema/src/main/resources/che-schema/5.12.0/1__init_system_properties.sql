--
--  [2012] - [2017] Codenvy, S.A.
--  All Rights Reserved.
--
-- NOTICE:  All information contained herein is, and remains
-- the property of Codenvy S.A. and its suppliers,
-- if any.  The intellectual and technical concepts contained
-- herein are proprietary to Codenvy S.A.
-- and its suppliers and may be covered by U.S. and Foreign Patents,
-- patents in process, and are protected by trade secret or copyright law.
-- Dissemination of this information or reproduction of this material
-- is strictly forbidden unless prior written permission is obtained
-- from Codenvy S.A..
--
-- System Properties ---------------------------------------------------------------------
CREATE TABLE che_system_property (
    property_name              VARCHAR(255)   NOT NULL,
    property_value             VARCHAR(255)   NOT NULL,
    property_modified_date     BIGINT         NOT NULL,

    PRIMARY KEY (property_name)
);
-- indexes
CREATE INDEX index_property_name ON che_system_property (property_name);
