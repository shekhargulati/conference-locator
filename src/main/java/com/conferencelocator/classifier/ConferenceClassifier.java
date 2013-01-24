package com.conferencelocator.classifier;

import net.sf.classifier4J.bayesian.BayesianClassifier;
import net.sf.classifier4J.bayesian.IWordsDataSource;
import net.sf.classifier4J.bayesian.SimpleWordsDataSource;

public abstract class ConferenceClassifier {

	public static boolean classify(String trainingData, String input)
			throws Exception {

		IWordsDataSource wds = new SimpleWordsDataSource();
		BayesianClassifier classifier = new BayesianClassifier(wds);
		classifier.teachMatch(trainingData);
		double classify = classifier.classify(input);
		return classify >= 0.7 ? true : false;

	}
}
