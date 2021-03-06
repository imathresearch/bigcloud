--
-- JBoss, Home of Professional Open Source
-- Copyright 2012, Red Hat, Inc., and individual contributors
-- by the @authors tag. See the copyright.txt in the distribution for a
-- full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements

insert into BC_user (username, firstname, lastname, organization, email, password) values ('ammartinez', 'Andrea', 'MartinezTT','iMath Research S.L.', 'ammartinez@ammartinez.com','NcIFc4uiQryugnBqeHSAtg==');
insert into BC_user (username, firstname, lastname, organization, email, password) values ('imath', 'Andrea', 'Martinez','iMath Research S.L.', 'ammartinez@imathresearch.com','kNg2mH9QDoEe/15QyPUjZQjzqBIjDyyH');
insert into BC_user (username, firstname, lastname, organization, email, password) values ('imathTest', 'Andrea', 'Martinez','iMath Research S.L.', 'andrea.mmt84@gmail.com','qNsToEtxtq/Ji2zMRwviSzeJ5JcyFDWB');


insert into BC_user (username, firstname, lastname, organization, email, password) values ('ipinyolTest', 'Isaac', 'Pinyol','iMath Research S.L.', 'ipinyol@imathresearch.com','IV0p34cDmXFCzA3p9V7/Hg==');
insert into BC_user (username, firstname, lastname, organization, email, password) values ('user52', 'User', 'User','S.L.', 'user@user.com','LTfe70E0fKpvIFLyLYoa2bQcuzutOY1j');

insert into Service (id, name, description) values (1 , 'Twitter Sentiment Analysis', 'Analyses the sentiment of tweets that match a specific term');
insert into Service (id, name, description) values (2 , 'Sales Prediction', 'Predicts the sales of several regions');

insert into Service_Instance (id, idService, idUser) values (1, 1, 'user52');
insert into Service_Instance (id, idService, idUser) values (2, 1, 'user52');
insert into Service_Instance (id, idService, idUser) values (3, 1, 'ammartinez');
insert into Service_Instance (id, idService, idUser) values (4, 1, 'ammartinez');
insert into Service_Instance (id, idService, idUser) values (5, 1, 'ipinyolTest');
insert into Service_Instance (id, idService, idUser) values (6, 1, 'ipinyolTest');
insert into Service_Instance (id, idService, idUser) values (7, 1, 'imath');
insert into Service_Instance (id, idService, idUser) values (8, 1, 'imath');
insert into Service_Instance (id, idService, idUser) values (9, 1, 'imathTest');
insert into Service_Instance (id, idService, idUser) values (10, 1, 'imathTest');

