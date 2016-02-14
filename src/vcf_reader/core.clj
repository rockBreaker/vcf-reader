(ns vcf-reader.core
  (:require [clojure.java.io :as io])
  (:import (ezvcard.io.text VCardReader)))

(def raw_data (io/file "resources/contacts.vcf"))

(defn vreader 
  [vcf-file]
  (VCardReader. vcf-file))

(defn remove-by [keyw all]
  (remove (fn [thing] (empty? (keyw thing))) all))

(defn emails 
  [vcard]
  (map (fn [email] (.getValue email)) (.getEmails vcard)))

(defn telephone-numbers
  [vcard]
  (map
   (fn [number] {:number (.getText number)
                 :types  (map #(.getValue %) (.getTypes number))})
   (.getTelephoneNumbers vcard)))

(defn categories 
  [vcard]
  (when-let [categories (.getCategories vcard)]
    (.getValues categories)))

(defn organizations
  [vcard]
  (when-let [org (.getOrganization vcard)]
    (.getValues org)))

(defn address
  [vcard]
  (when-let [adds (.getAddresses vcard)]
    (map 
     (fn [add]
       {:country         (.getCountry add)
        :locality        (.getLocality add)
        :po-box          (.getPoBox add)
        :postal-code     (.getPostalCode add)
        :region          (.getRegion add)})
     adds)))

(defn vname 
  [vard]
  (when-let [vname (.getStructuredName vcard)]
    {:first-name  (.getGiven vname)
     :last-name   (.getFamily vname)
     :title       (.getPrefixes vname)}))

(defn information 
"we could start with a base map, then -> merge all new maps"
  [vcard]
  {:name             (vname vcard)
   :email            (emails vcard)
   :telephone-number (telephone-numbers vcard)
   :organizations    (organizations vcard)
   :categories       (categories vcard)
   :address          (address vcard)})

(defn all-vcards 
  [vcf-file]
  (let [vreader (vreader vcf-file)
        vcards (.readAll vreader)]
    (map information vcards)))


;;name
;;first name
;;last name
;;telephone
;;mobile
;;address
;;getCategories
