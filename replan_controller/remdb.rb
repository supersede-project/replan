# print RestClient.get("http://62.14.219.13:8280/replan/projects/1").body

p = Project.first
p.releases.destroy_all
p.features.destroy_all
p.resources.destroy_all
p.skills.destroy_all

## Skills
ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'skills'")
skills = JSON.parse(RestClient.get("http://62.14.219.13:8280/replan/projects/1/skills").body)
skills.each do |s|
   ns = p.skills.create(name: s["name"], description: s["description"])
   if ns.id != s["id"]
      ns.id = s["id"]
      ns.save
   end
end

## Resources
ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'resources'")
resources = JSON.parse(RestClient.get("http://62.14.219.13:8280/replan/projects/1/resources").body)
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
ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'features'")
features = JSON.parse(RestClient.get("http://62.14.219.13:8280/replan/projects/1/features").body)
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
   f["depends_on"].each do |d|
     nf.depends_on << Feature.find(d["id"])
   end
end

## Releases 
ActiveRecord::Base.connection.execute("DELETE from sqlite_sequence where name = 'releases'")
releases = JSON.parse(RestClient.get("http://62.14.219.13:8280/replan/projects/1/releases").body)
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
   uri = "http://62.14.219.13:8280/replan/projects/1/releases/"+ nl.id.to_s + "/features"
   JSON.parse(RestClient.get(uri).body).each do |f|
     nl.features << Feature.find(f["id"])
   end
end