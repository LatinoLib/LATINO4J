/*
 * Copyright 2013 Michal Hlavac <hlavki@hlavki.eu>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.hlavki.text.lemmagen.api;

/**
 *
 * @author Michal Hlavac {@literal <hlavki@hlavki.eu>}
 */
public interface TrainableLemmatizer extends Lemmatizer {

    void addExample(String word, String lemma);

    void addExample(String word, String lemma, double weight);

    void addExample(String word, String lemma, double weight, String msd);

    void buildModel();
}
