# print RestClient.get("#{endpoint}/projects/#{i}").body

endpoint = "http://62.14.219.13:8280/replan"
#endpoint = "http://platform.supersede.eu:8280/replan"

Plan.destroy_all
Release.destroy_all
Feature.destroy_all
Resource.destroy_all
Skill.destroy_all

ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'skills'")
ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'resources'")
ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'features'")
ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'releases'")

[1,2,3].each do |i|

   p = Project.find(i)
   puts p.name
   ## Skills
   skills = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/skills").body)
   skills.each do |s|
      ns = p.skills.create(name: s["name"], description: s["description"])
      if ns.id != s["id"]
         ns.id = s["id"]
         ns.save
      end
   end
   
   ## Resources
   resources = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/resources").body)
   resources.each do |r|
      nr = p.resources.create(name: r["name"], description: r["description"], availability: r["availability"].to_f)
      if nr.id != r["id"]
         nr.id = r["id"]
         nr.save
      end
      r["skills"].each do |rs|
        nr.skills << Skill.find(rs["id"])
      end
   end
   
   ## Features
   dependencies = Hash.new
   features = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/features").body)
   features.each do |f|
      nf = p.features.create(code: f["code"], name: f["name"], description: f["description"], \
                          deadline: f["deadline"], priority: f["priority"], \
                          effort: f["effort"].to_f)
      if nf.id != f["id"]
        nf.id = f["id"]
        nf.save
      end
      f["required_skills"].each do |rs|
        nf.required_skills << Skill.find(rs["id"])
      end
      depi = Array.new
      f["depends_on"].each do |d|
        depi << d["id"]
      end
      dependencies[nf.id] = depi unless depi.empty? 
   end
   
   dependencies.each do |key, value|
     fi = Feature.find(key)
     value.each do |id|
        fi.depends_on << Feature.find(id)
     end
   end
   
   ## Releases 
   releases = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/releases").body)
   releases.each do |l|
      nl = p.releases.create(name: l["name"], description: l["description"], \
                          deadline: l["deadline"], starts_at: l["starts_at"])
      if nl.id != l["id"]
        nl.id = l["id"]
        nl.save
      end  
      l["resources"].each do |r|
        nl.resources << Resource.find(r["id"])
      end
      uri = "#{endpoint}/projects/#{i}/releases/"+ nl.id.to_s + "/features"
      JSON.parse(RestClient.get(uri).body).each do |f|
        nl.features << Feature.find(f["id"])
      end
      nl.plan.destroy unless nl.plan.nil?
   end

end