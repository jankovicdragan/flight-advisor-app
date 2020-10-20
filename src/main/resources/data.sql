DROP TABLE IF EXISTS User;
 
CREATE TABLE User (
  id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  first_name VARCHAR(250) NOT NULL,
  last_name VARCHAR(250) NOT NULL,
  username VARCHAR(250) NOT NULL,
  password VARCHAR(250) NOT NULL,
  salt VARCHAR(250) NOT NULL,
  role VARCHAR(20) NOT NULL,
  is_account_non_expired BOOLEAN,
  is_account_non_locked BOOLEAN,
  is_credentials_non_expired BOOLEAN,
  is_enabled BOOLEAN
);

INSERT INTO User(first_name,last_name,username,password,salt,role,is_account_non_expired,is_account_non_locked,is_credentials_non_expired,is_enabled)
VALUES ('admin', 'admin', 'admin', '$2a$10$TqA01y5QG5HztgmE2sxRB.BIK6.qKhB2cxALjttZUaslvtAVwPWzW', 'TqA01y5QG5HztgmE2sxRB.', 'ADMIN', true, true, true, true);

INSERT INTO User(first_name,last_name,username,password,salt,role,is_account_non_expired,is_account_non_locked,is_credentials_non_expired,is_enabled)
VALUES ('guest', 'guest', 'guest', '$2a$10$vcVYz9hZFa0EUDAGh.Gob.8i3Symd/CVIQBKL2FCjLpkww25iByUm', 'vcVYz9hZFa0EUDAGh.Gob.', 'REGULAR', true, true, true, true);