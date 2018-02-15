# -*- coding: utf-8 -*-
#!/usr/bin/python

import random
import sys


if __name__ == "__main__":



        # Lecture du fichier et decomposition du fichier en mots


        file = open("defaultoutput.txt",'r')
        fileText = file.read().replace("Négatif","Negatif").decode('utf-8')
        tweets = fileText.split("\n\n")[1:]
        nbTweets = len(tweets)
        nbSample = input("The sentiment analysis algorithm has analyzed  {} tweets. What size of sample do you want to make a performance test on ? ".format(nbTweets))
        tweetsSample = random.sample(tweets,nbSample)
        neg_neg = 0
        neg_neu = 0
        neg_pos = 0
        neu_neg = 0
        neu_neu = 0
        neu_pos = 0
        pos_neg = 0
        pos_neu = 0
        pos_pos = 0
        print("For each tweet, write if its meaning is positive, negative or neutral (pos/neg/neu)\n")
        for tweets in tweetsSample:
            opinion = ""
            tweet = tweets.split("\n")[0][7:]
            result = tweets.split("\n")[-1][7:]
            print(tweet.encode('ascii', 'ignore'))
            while (opinion!="pos\n" and opinion!="neu\n" and opinion!="neg\n"):
                opinion = sys.stdin.readline()
            if (opinion == "pos\n" and result == "Positif"):
                pos_pos += 1
            elif (opinion == "pos\n" and result == "Neutre"):
                pos_neu += 1
            elif (opinion == "pos\n" and result == "Negatif"):
                pos_neg += 1
            elif (opinion == "neu\n" and result == "Positif"):
                neu_pos += 1
            elif (opinion == "neu\n" and result == "Neutre"):
                neu_neu += 1
            elif (opinion == "neu\n" and result == "Negatif"):
                neu_neg += 1
            elif (opinion == "neg\n" and result == "Positif"):
                neg_pos += 1
            elif (opinion == "neg\n" and result == "Neutre"):
                neg_neu += 1
            elif (opinion == "neg\n" and result == "Negatif"):
                neg_neg += 1
            else:
                print(opinion);
            print(result);
            print("\n")
        print("     pos   neu  neg")
        print("pos  {}%    {}%    {}%".format(pos_pos*100/nbSample, pos_neu*100/nbSample, pos_neg*100/nbSample))
        print("neu  {}%    {}%    {}%".format(neu_pos*100/nbSample, neu_neu*100/nbSample, neu_neg*100/nbSample))
        print("neg  {}%    {}%    {}%".format(neg_pos*100/nbSample, neg_neu*100/nbSample, neg_neg*100/nbSample))
        print("\n")
        print("true rate : {}% --- false rate : {}%".format((pos_pos+neu_neu+neg_neg)*100/nbSample,100-(pos_pos+neu_neu+neg_neg)*100/nbSample))
        print("\n")