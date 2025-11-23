import React from 'react';
import { RarityBadge } from './ui/RarityBadge';

interface MobCardProps {
    mob: {
        name: string;
        desc: string;
        danger: string;
        rarity: string;
    };
}

export const MobCard: React.FC<MobCardProps> = ({ mob }) => (
    <div className={`border-l-2 pl-4 bg-zinc-900/50 p-2 border-l-${mob.rarity === 'comun' ? 'gray' : mob.rarity === 'raro' ? 'blue' : mob.rarity === 'epico' ? 'purple' : mob.rarity === 'legendario' ? 'yellow' : 'red'}-600`}>
        <div className="flex justify-between items-center mb-1">
            <strong className="text-lg text-gray-200">{mob.name}</strong>
            <RarityBadge rarity={mob.rarity} />
        </div>
        <p className="text-gray-500 text-sm mb-1">{mob.desc}</p>
        <div className="text-xs text-red-500 tracking-widest">{mob.danger}</div>
    </div>
);
